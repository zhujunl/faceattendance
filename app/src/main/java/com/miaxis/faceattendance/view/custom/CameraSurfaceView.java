package com.miaxis.faceattendance.view.custom;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.miaxis.faceattendance.manager.CameraManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.ToastManager;

public class CameraSurfaceView extends TextureView {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
//        this.setSurfaceTextureListener(listner);
        CameraManager.getInstance().openCamera(this,cameraListener);
    }
    private CameraManager.OnCameraOpenListener cameraListener = new CameraManager.OnCameraOpenListener() {
        @Override
        public void onCameraOpen(Camera.Size previewSize, String message) {
            if (previewSize == null) {
                try {
                    ToastManager.toast(mContext,message,ToastManager.ERROR);
                    Log.e("asd", "onCameraOpen:开始修复摄像头卡顿");
                    CameraManager.getInstance().closeCamera();
                    GpioManager.getInstance().closeCameraGpio();
                    Thread.sleep(800);
                    GpioManager.getInstance().openCameraGpio();
                    CameraManager.getInstance().openCamera(CameraSurfaceView.this, cameraListener);
                    Log.e("asd", "onCameraOpen:结束修复摄像头卡顿");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCameraError() {
            try {
                Log.e("asd", "onCameraError:开始修复摄像头卡顿");
                CameraManager.getInstance().closeCamera();
                GpioManager.getInstance().closeCameraGpio();
                Thread.sleep(800);
                GpioManager.getInstance().openCameraGpio();
                CameraManager.getInstance().openCamera(CameraSurfaceView.this, cameraListener);
                Log.e("asd", "onCameraError:结束修复摄像头卡顿");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=width* CameraManager.PIC_HEIGHT/CameraManager.PIC_WIDTH;
        setMeasuredDimension(width,height);
    }

    //    TextureView.SurfaceTextureListener listner=new SurfaceTextureListener() {
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            CameraManager.getInstance().resetRetryTime();
//            CameraManager.getInstance().openCamera(CameraSurfaceView.this,surface,mPreviewCallback,cameraListener);
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            CameraManager.getInstance().closeCamera();
//            return false;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//        }
//    };

//    Camera.PreviewCallback mPreviewCallback=new Camera.PreviewCallback() {
//        @Override
//        public void onPreviewFrame(byte[] data, Camera camera) {
//            FaceManager.getInstance().setLastVisiblePreviewData(data);
//        }
//    };
//    public interface OnCameraSizeSelect {
//        void resetSize();
//    }

}
