package com.miaxis.faceattendance.manager;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.miaxis.faceattendance.event.OpenCameraEvent;

import org.greenrobot.eventbus.EventBus;

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

    private Camera mCamera;

    public void openCamera(SurfaceHolder holder, Camera.PreviewCallback previewCallback) {
        try {
            EventBus.getDefault().post(new OpenCameraEvent(PRE_WIDTH, PRE_HEIGHT));
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
//            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(180);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
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
