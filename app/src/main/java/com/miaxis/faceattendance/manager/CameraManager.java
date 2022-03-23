package com.miaxis.faceattendance.manager;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.view.TextureView;

import com.miaxis.faceattendance.constant.Constants;
import com.miaxis.faceattendance.event.OpenCameraEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class CameraManager {

    private CameraManager() {
    }

    public static CameraManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CameraManager instance = new CameraManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static final int PRE_WIDTH = 640;
    public static final int PRE_HEIGHT = 480;
    public static final int PIC_WIDTH = 640;
    public static final int PIC_HEIGHT = 480;
    private static final int RETRY_TIMES = 3;

    public static int ORIENTATION = 180;
    private Camera mCamera;
    private int retryTime = 0;

    public void resetRetryTime() {
        this.retryTime = 0;
    }

    public void openCamera(TextureView textureView, SurfaceTexture holder, Camera.PreviewCallback previewCallback) {
        try {
            EventBus.getDefault().post(new OpenCameraEvent(PRE_WIDTH, PRE_HEIGHT));
            for (int i = 0; i < RETRY_TIMES; i++) {
                if (mCamera==null){
                    try {
                        mCamera = Camera.open(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (mCamera!=null){
                        break;
                    }
                    SystemClock.sleep(500);
                }
            }
            Camera.Parameters parameters = mCamera.getParameters();
//            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            int maxWidth = 0;
            int maxHeight = 0;
            for (Camera.Size size : supportedPreviewSizes) {
                maxWidth = Math.max(size.width, maxWidth);
                maxHeight = Math.max(size.height, maxHeight);
            }
            ORIENTATION = maxWidth * maxHeight >= (200 * 10000) ? 0 : (!Constants.VERSION?0:180);
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(ORIENTATION);
            mCamera.setPreviewTexture(holder);
            textureView.setRotationY(ORIENTATION);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            new Thread(() -> {
                if (retryTime <= RETRY_TIMES) {
                    GpioManager.getInstance().resetCameraGpio();
                    retryTime++;
                    openCamera(textureView,holder, previewCallback);
                }
            }).start();
        }
    }

    public void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture(Camera.PictureCallback jpeg) {
        mCamera.takePicture(null, null, jpeg);
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

}
