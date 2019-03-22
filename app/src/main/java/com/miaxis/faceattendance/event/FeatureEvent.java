package com.miaxis.faceattendance.event;

import org.zz.faceapi.MXFaceInfoEx;

public class FeatureEvent {

    public final static int CAMERA_FACE = 1;
    public final static int IMAGE_FACE = 2;

    private int mode;
    private byte[] feature;
    private MXFaceInfoEx mxFaceInfoEx;
    private byte[] image;
    private int width;
    private int height;
    private String message;

    public FeatureEvent(int mode, String message) {
        this.mode = mode;
        this.message = message;
    }

    public FeatureEvent(int mode, byte[] feature, MXFaceInfoEx mxFaceInfoEx) {
        this.mode = mode;
        this.feature = feature;
        this.mxFaceInfoEx = mxFaceInfoEx;
    }

    public FeatureEvent(int mode, byte[] feature, byte[] image, int width, int height) {
        this.mode = mode;
        this.feature = feature;
        this.image = image;
        this.width = width;
        this.height = height;
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

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
}
