package com.miaxis.faceattendance.manager;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.view.TextureView;

import com.miaxis.faceattendance.constant.Constants;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;

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
    private CameraSurfaceView mCameraSurfaceView;
    private  SurfaceTexture surfaceTexture = null;
    private OnCameraOpenListener listener;

    private volatile boolean monitorFlag = false;
    private long lastCameraCallBackTime;
    private MonitorThread monitorThread;



    public void resetRetryTime() {
        this.retryTime = 0;
    }

    public synchronized void openCamera(@NonNull TextureView textureView, @NonNull OnCameraOpenListener listener){
        this.listener=listener;
        try {
            openCamera();
            openMonitor();
            textureView.setSurfaceTextureListener(textureListener);
            if (surfaceTexture != null) {
                mCamera.setPreviewTexture(surfaceTexture);
            }
            textureView.setRotationY(ORIENTATION);
            listener.onCameraOpen(mCamera.getParameters().getPreviewSize(), "");
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCameraOpen(null, "异常: "+e);
        }
    }

    public void openCamera() {
//        try {
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
//            mCamera.setPreviewTexture(holder);
//            textureView.setRotationY(ORIENTATION);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
//        } catch (Exception e) {
//            e.printStackTrace();
////            new Thread(() -> {
////                if (retryTime <= RETRY_TIMES) {
////                    GpioManager.getInstance().resetCameraGpio();
////                    retryTime++;
////                    openCamera(textureView, holder,previewCallback,listener);
////                }
////            }).start();
//        }
    }

    public void closeCamera() {
        try {
            closeMonitor();
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

    public TextureView.SurfaceTextureListener textureListener=new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    surfaceTexture=surface;
                    if (mCamera!=null){
                        mCamera.setPreviewTexture(surfaceTexture);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                //            closeCamera();
                //            clearSur();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };


    Camera.PreviewCallback mPreviewCallback= (data, camera) -> {
        lastCameraCallBackTime = System.currentTimeMillis();
        FaceManager.getInstance().setLastVisiblePreviewData(data);
    };

    public void takePicture(Camera.PictureCallback jpeg) {
        mCamera.takePicture(null, null, jpeg);
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
            openMonitor();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            closeMonitor();
        }
    }

    private class MonitorThread extends Thread {
        @Override
        public void run() {
            lastCameraCallBackTime = System.currentTimeMillis();
            while (!interrupted()) {
                try {
                    Thread.sleep(1000);
                    if (monitorFlag) {
                        long cur = System.currentTimeMillis();
                        if ((cur - lastCameraCallBackTime) >= (4 * 1000L)) {
                            if (listener != null) {
                                listener.onCameraError();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openMonitor() {
        lastCameraCallBackTime = System.currentTimeMillis();
        monitorFlag = true;
        if (monitorThread == null) {
            monitorThread = new MonitorThread();
            monitorThread.start();
        }
    }

    private void closeMonitor() {
        monitorFlag = false;
    }



    public interface OnCameraOpenListener {
        void onCameraOpen(Camera.Size previewSize, String message);
        void onCameraError();
    }
}
