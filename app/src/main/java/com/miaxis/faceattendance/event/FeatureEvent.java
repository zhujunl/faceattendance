package com.miaxis.faceattendance.event;

import com.miaxis.faceattendance.model.entity.RGBImage;

import org.zz.faceapi.MXFaceInfoEx;

public class FeatureEvent {

    public final static int CAMERA_FACE = 1;
    public final static int IMAGE_FACE = 2;

    private int mode;
    private byte[] feature;
    private MXFaceInfoEx mxFaceInfoEx;
    private String message;
    private RGBImage rgbImage;
    private String mark;

    public FeatureEvent(int mode, String message) {
        this.mode = mode;
        this.message = message;
    }

    public FeatureEvent(int mode, RGBImage rgbImage, byte[] feature, MXFaceInfoEx mxFaceInfoEx, String mark) {
        this.mode = mode;
        this.rgbImage = rgbImage;
        this.feature = feature;
        this.mxFaceInfoEx = mxFaceInfoEx;
        this.mark = mark;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public MXFaceInfoEx getMxFaceInfoEx() {
        return mxFaceInfoEx;
    }

    public void setMxFaceInfoEx(MXFaceInfoEx mxFaceInfoEx) {
        this.mxFaceInfoEx = mxFaceInfoEx;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RGBImage getRgbImage() {
        return rgbImage;
    }

    public void setRgbImage(RGBImage rgbImage) {
        this.rgbImage = rgbImage;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
