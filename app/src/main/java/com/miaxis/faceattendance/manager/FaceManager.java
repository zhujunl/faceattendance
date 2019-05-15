package com.miaxis.faceattendance.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.miaxis.faceattendance.event.DrawRectEvent;
import com.miaxis.faceattendance.event.FeatureEvent;
import com.miaxis.faceattendance.event.InitFaceEvent;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.zz.faceapi.MXFaceAPI;
import org.zz.faceapi.MXFaceInfoEx;
import org.zz.jni.mxImageTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class FaceManager {

    private FaceManager() {
        mxFaceAPI = new MXFaceAPI();
        dtTool = new mxImageTool();
    }

    public static FaceManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final FaceManager instance = new FaceManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

//    public static final int ZOOM_WIDTH = 320;
//    public static final int ZOOM_HEIGHT = 240;
    public static final int ZOOM_WIDTH = 640;
    public static final int ZOOM_HEIGHT = 480;
    public static final int DEFAULT_INTERVEL_TIME = 1500;
    private static final int MAX_FACE_NUM = 3;
    private static final Byte lock1 = 1;
    private static final Byte lock2 = 2;
    private volatile static boolean detectFlag = false;
    private volatile static boolean extractWorking = false;
    private volatile static boolean activeVerify = true;
    private boolean delay = false;
    private static int intervelTime = 1500;
    private long last = 0;

    private MXFaceAPI mxFaceAPI;
    private mxImageTool dtTool;

    private List<Person> personList;

    /**
     * 初始化人脸算法
     * @param context 设备上下文
     * @param szModelPath 人脸模型文件目录
     * @param licencePath 授权文件路径
     * @return 状态码
     */
    public int initFaceST(Context context, String szModelPath, String licencePath) {
        final String sLicence = FileUtil.readLicence(licencePath);
        if (TextUtils.isEmpty(sLicence)) {
            return InitFaceEvent.ERR_LICENCE;
        }
        int re = initFaceModel(context, szModelPath);
        if (re == 0) {
            re = mxFaceAPI.mxInitAlg(context, szModelPath, sLicence);
        }
        return re;
    }

    /**
     * 摄像头回流开关
     * @param detectFlag
     */
    public void setVerify(boolean detectFlag) {
        FaceManager.detectFlag = detectFlag;
    }

    /**
     * 是否启用延迟操作
     * @param delay
     */
    public void setDelay(boolean delay) {
        this.delay = delay;
    }

    public void setIntervelTime(int intervelTime) {
        FaceManager.intervelTime = intervelTime;
    }

    public void setActiveVerify(boolean activeVerify) {
        FaceManager.activeVerify = activeVerify;
    }

    /**
     * 摄像头onPreviewFrame回调，通过EventBus回调事件
     * @param data onPreviewFrame-data
     */
    public void verify(byte[] data) {
        if (!detectFlag || !checkDelay()) {
            return;
        }
        byte[] zoomedRgbData = cameraPreviewConvert(data, CameraManager.PRE_WIDTH, CameraManager.PRE_HEIGHT, 180, ZOOM_WIDTH, ZOOM_HEIGHT);
        if (zoomedRgbData == null) {
            EventBus.getDefault().post(new DrawRectEvent(0, null));
            return;
        }
        int[] pFaceNum = new int[] {MAX_FACE_NUM};
        MXFaceInfoEx[] pFaceBuffer = makeFaceContainer(pFaceNum[0]);
        boolean result = faceDetect(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum, pFaceBuffer);
        if (result) {
            result = faceQuality(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum[0], pFaceBuffer);
            EventBus.getDefault().post(new DrawRectEvent(pFaceNum[0], pFaceBuffer));
            if (result
                    && pFaceBuffer[0].quality > ConfigManager.getInstance().getConfig().getVerifyQualityScore()
                    && !extractWorking
                    && pFaceBuffer[0].eyeDistance > 25) {
                extractWorking = true;
                byte[] feature = extractFeature(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceBuffer[0]);
                if (feature != null && detectFlag) {
                    if (activeVerify) {
                        verifyPersonFace(new RGBImage(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT), feature);
                    } else {
                        EventBus.getDefault().post(new FeatureEvent(FeatureEvent.CAMERA_FACE, new RGBImage(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT), feature, pFaceBuffer[0]));
                    }
                }
                extractWorking = false;
            } else {
                EventBus.getDefault().post(new DrawRectEvent(-1, null));
            }
        } else {
            EventBus.getDefault().post(new DrawRectEvent(0, null));
        }
    }

    private synchronized void verifyPersonFace(RGBImage rgbImage, byte[] feature) {
        if (personList == null) {
            personList = PersonModel.loadAllPerson();
        }
        float maxScore = 0;
        Person maxScorePerson = null;
        for (Person person : personList) {
            float score = matchFeature(feature, Base64.decode(person.getFaceFeature(), Base64.NO_WRAP));
            if (score > maxScore) {
                maxScore = score;
                maxScorePerson = person;
            }
        }
        if (maxScore > ConfigManager.getInstance().getConfig().getVerifyScore()) {
            EventBus.getDefault().post(new VerifyPersonEvent(maxScorePerson, rgbImage, maxScore));
        }
    }

    public void clearVerifyList() {
        personList = null;
    }

    /**
     * 通过Bitmap图像获取特征
     * @param bitmap
     */
    public void getFeatureByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        getFeatureByImage(outputStream.toByteArray(), bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 通过图像（文件格式）获取特征，完成后发送EventBus事件
     * @param data
     * @param width
     * @param height
     */
    public void getFeatureByImage(byte[] data, int width, int height) {
        byte[] rgbData = imageFileDecode(data, width, height);
        if (rgbData == null) {
            EventBus.getDefault().post(new FeatureEvent(FeatureEvent.IMAGE_FACE, "提取RGB图像数据失败"));
            return;
        }
        String message = "未检测到人脸";
        int[] pFaceNum = new int[] {0};
        MXFaceInfoEx[] pFaceBuffer = makeFaceContainer(MAX_FACE_NUM);
        boolean result = faceDetect(rgbData, width, height, pFaceNum, pFaceBuffer);
        if (result) {
            if (pFaceNum[0] == 1) {
                result = faceQuality(rgbData, width, height, pFaceNum[0], pFaceBuffer);
                if (result
                        && pFaceBuffer[0].quality > ConfigManager.getInstance().getConfig().getRegisterQualityScore()
                        && pFaceBuffer[0].eyeDistance > 25) {
                    byte[] feature = extractFeature(rgbData, width, height, pFaceBuffer[0]);
                    if (feature != null) {
                        EventBus.getDefault().post(new FeatureEvent(FeatureEvent.IMAGE_FACE, new RGBImage(rgbData, width, height), feature, pFaceBuffer[0]));
                        return;
                    }
                    message = "提取人脸特征失败";
                } else {
                    message = "人脸质量评分过低";
                }
            } else if (pFaceNum[0] > 1) {
                message = "检测到多张人脸";
            }
        }
        EventBus.getDefault().post(new FeatureEvent(FeatureEvent.IMAGE_FACE, message));
    }

    /**
     * 通过图像（文件格式）获取特征
     * @param fileData
     * @return
     */
    public byte[] getFeatureByFileImage(byte[] fileData, StringBuilder stringBuilder) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
        if (bitmap == null) {
            stringBuilder.append("图像转码失败，请确保传入的是完整的图像文件");
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//        if ((float) width / height != 0.75f) {
//            return null;
//        }
        bitmap.recycle();
        byte[] rgbData = imageFileDecode(fileData, width, height);
        if (rgbData == null) {
            stringBuilder.append("图像提取RGB失败");
            return null;
        }
        int[] pFaceNum = new int[] {1};
        MXFaceInfoEx[] pFaceBuffer = makeFaceContainer(MAX_FACE_NUM);
        boolean result = faceDetect(rgbData, width, height, pFaceNum, pFaceBuffer);
        if (result) {
            result = faceQuality(rgbData, width, height, pFaceNum[0], pFaceBuffer);
            if (result
                    && pFaceBuffer[0].quality > ConfigManager.getInstance().getConfig().getRegisterQualityScore()
                    && pFaceBuffer[0].eyeDistance > 25) {
                byte[] feature = extractFeature(rgbData, width, height, pFaceBuffer[0]);
                if (feature != null) {
                    stringBuilder.append("图像提取特征成功");
                    return feature;
                } else {
                    stringBuilder.append("图像提取特征失败");
                    return null;
                }
            } else {
                stringBuilder.append("图像质量评分过低");
                return null;
            }
        } else {
            stringBuilder.append("未检测到人脸");
            return null;
        }
    }

    /**
     * 比对特征，人证比对0.7，人像比对0.8
     * @param alpha
     * @param beta
     * @return
     */
    public float matchFeature(byte[] alpha, byte[] beta) {
        if (alpha != null && beta != null) {
            float[] score = new float[1];
            int re = mxFaceAPI.mxFeatureMatch(alpha, beta, score);
            if (re == 0) {
                return score[0];
            }
            return -1;
        }
        return 0;
    }

    /**
     * 将640 * 480的图像按中心位置截取为360 * 480的图像
     */
    public Bitmap tailoringFace(Bitmap bitmap, MXFaceInfoEx mxFaceInfoEx) throws IllegalArgumentException {
        if (mxFaceInfoEx.x < 140 || mxFaceInfoEx.x > 500) {
            return null;
        }
        return Bitmap.createBitmap(bitmap, 141, 0, 360, 480);//截取
    }

    /**
     * 释放
     */
    public void close() {
        if (mxFaceAPI != null) {
            mxFaceAPI.mxFreeAlg();
        }
    }

    /**
     * 保存rgb裸图像数据到文件
     * @param data
     * @param width
     * @param height
     * @return
     */
    public boolean saveRGBImageData(String path, byte[] data, int width, int height) {
        int result = dtTool.ImageSave(path, data, width, height, 3);
        if (result == 1) {
            return true;
        }
        return false;
    }

    public byte[] imageEncode(byte[] rgbBuf, int width, int height) {
        byte[] fileBuf = new byte[width * height * 4];
        int[] fileLength = new int[] {0};
        int re = dtTool.ImageEncode(rgbBuf, width, height, ".jpg", fileBuf, fileLength);
        if (re == 1 && fileLength[0] != 0) {
            byte[] fileImage = new byte[fileLength[0]];
            System.arraycopy(fileBuf, 0, fileImage, 0, fileImage.length);
            return fileImage;
        } else {
            return null;
        }
    }

    private boolean checkDelay() {
        if (delay) {
            long res = System.currentTimeMillis() - last;
            if (res > intervelTime || res < 0) {
                last = System.currentTimeMillis();
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 图像文件解码成RGB裸数据
     * @param data
     * @param width
     * @param height
     * @return
     */
    private byte[] imageFileDecode(byte[] data, int width, int height) {
        byte[] rgbData = new byte[width * height * 3];
        int[] oX = new int[1];
        int[] oY = new int[1];
        int result = dtTool.ImageDecode(data, data.length, rgbData, oX, oY);
        if (result > 0) {
            return rgbData;
        }
        return null;
    }

    /**
     * 摄像头预览数据转换
     * @param data 摄像头onPreviewFrame-data
     * @param width 摄像头实际分辨率-宽
     * @param height 摄像头实际分辨率-高
     * @param orientation 旋转角度
     * @param zoomWidth 实际分辨率旋转压缩后的宽度
     * @param zoomHeight 实际分辨率旋转压缩后的高度
     * @return
     */
    private byte[] cameraPreviewConvert(byte[] data, int width, int height, int orientation, int zoomWidth, int zoomHeight) {
        // 原始YUV数据转换RGB裸数据
        byte[] rgbData = new byte[width * height * 3];
        dtTool.YUV2RGB(data, width, height, rgbData);
        int[] rotateWidth = new int[1];
        int[] rotateHeight = new int[1];
        // 旋转相应角度
        int re = dtTool.ImageRotate(rgbData, width, height, orientation, rgbData, rotateWidth, rotateHeight);
        if (re != 1) {
            Log.e("asd", "旋转失败");
            return null;
        }
        //镜像后画框位置按照正常坐标系，不镜像的话按照反坐标系也可画框
//        re = dtTool.ImageFlip(rgbData, rotateWidth[0], rotateHeight[0], 1, rgbData);
//        if (re != 1) {
//            Log.e("asd", "镜像失败");
//            return null;
//        }
        // RGB数据压缩到指定宽高
        byte[] zoomedRgbData = new byte[zoomWidth * zoomHeight * 3];
        re = dtTool.Zoom(rgbData, rotateWidth[0], rotateHeight[0], 3, zoomWidth, zoomHeight, zoomedRgbData);
        if (re != 1) {
            Log.e("asd", "压缩失败");
            return null;
        }
        return zoomedRgbData;
    }

    /**
     * 组装人脸信息存储容器数组
     * @param size
     * @return
     */
    private MXFaceInfoEx[] makeFaceContainer(int size) {
        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[size];
        for (int i = 0; i < size; i++) {
            pFaceBuffer[i] = new MXFaceInfoEx();
        }
        return pFaceBuffer;
    }

    /**
     * 检测人脸信息
     * @param rgbData RGB裸图像数据
     * @param width 图像数据宽度
     * @param height 图像数据高度
     * @param faceNum native输出，检测到的人脸数量
     * @param faceBuffer native输出，人脸信息
     * @return true - 算法执行成功，并且检测到人脸，false - 算法执行失败，或者执行成功但是未检测到人脸
     */
    private boolean faceDetect(byte[] rgbData, int width, int height, int[] faceNum, MXFaceInfoEx[] faceBuffer) {
        synchronized (lock2) {
            int result = mxFaceAPI.mxDetectFace(rgbData, width, height, faceNum, faceBuffer);
            return result == 0 && faceNum[0] > 0;
        }
    }

    /**
     * 人脸质量检测
     * @param rgbData RGB裸图像数据
     * @param width 图像数据宽度
     * @param height 图像数据高度
     * @param faceNum 检测到人脸数量
     * @param faceBuffer 输入，人脸检测结果，native输出，根据瞳距进行从大到小排序
     * @return
     */
    private boolean faceQuality(byte[] rgbData, int width, int height, int faceNum, MXFaceInfoEx[] faceBuffer) {
        int result = mxFaceAPI.mxFaceQuality(rgbData, width, height, faceNum, faceBuffer);
        return result == 0;
    }

    /**
     * YUV格式图像数据提取人脸特征
     * @param pImage
     * @param width
     * @param height
     * @param faceInfo
     * @return
     */
    private byte[] extractFeatureYUV(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxFeatureExtractYUV(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
     * RGB裸图像数据提取人脸特征
     * @param pImage
     * @param width
     * @param height
     * @param faceInfo
     * @return
     */
    private byte[] extractFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            int result = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            return result == 0 ? feature : null;
        }
    }

    /**
     * 拷贝人脸模型文件
     * @param context
     * @param modelPath
     * @return
     */
    private int initFaceModel(Context context, String modelPath) {
        String hsLibDirName = "zzFaceModel";
        String modelFile1 = "MIAXISFaceDetector5.1.2.m9d6.640x480.ats";
        String modelFile2 = "MIAXISFaceDewave1.1.PA.raw.ats";
        String modelFile3 = "MIAXISFaceRecognizer5.0.RN30.m5d14.ID.ats";
        String modelFile4 = "MIAXISPointDetector5.0.pts5.ats";
        String modelFile5 = "MAIXISFaceQualityDlib68.dat";
        File modelDir = new File(modelPath);
        if (modelDir.exists()) {
            if (!new File(modelDir + File.separator + modelFile1).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile1, modelDir + File.separator + modelFile1);
            }
            if (!new File(modelDir + File.separator + modelFile2).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile2, modelDir + File.separator + modelFile2);
            }
            if (!new File(modelDir + File.separator + modelFile3).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile3, modelDir + File.separator + modelFile3);
            }
            if (!new File(modelDir + File.separator + modelFile4).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile4, modelDir + File.separator + modelFile4);
            }
            if (!new File(modelDir + File.separator + modelFile5).exists()) {
                FileUtil.copyAssetsFile(context, hsLibDirName + File.separator + modelFile5, modelDir + File.separator + modelFile5);
            }
            return 0;
        } else {
            return -1;
        }
    }

}
