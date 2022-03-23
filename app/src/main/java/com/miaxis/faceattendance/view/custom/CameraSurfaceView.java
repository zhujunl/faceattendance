package com.miaxis.faceattendance.view.custom;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.miaxis.faceattendance.manager.CameraManager;
import com.miaxis.faceattendance.manager.FaceManager;

public class CameraSurfaceView extends TextureView {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setSurfaceTextureListener(listner);
    }

    TextureView.SurfaceTextureListener listner=new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            CameraManager.getInstance().resetRetryTime();
            CameraManager.getInstance().openCamera(CameraSurfaceView.this,surface,mPreviewCallback);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    Camera.PreviewCallback mPreviewCallback=new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            FaceManager.getInstance().setLastVisiblePreviewData(data);
        }
    };
    public interface OnCameraSizeSelect {
        void resetSize();
    }

}
