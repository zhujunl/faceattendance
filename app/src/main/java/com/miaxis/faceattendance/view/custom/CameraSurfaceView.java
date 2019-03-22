package com.miaxis.faceattendance.view.custom;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.miaxis.faceattendance.manager.CameraManager;
import com.miaxis.faceattendance.manager.FaceManager;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraManager.getInstance().openCamera(mSurfaceHolder, this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraManager.getInstance().closeCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        FaceManager.getInstance().verify(data);
    }

    public interface OnCameraSizeSelect {
        void resetSize();
    }

}
