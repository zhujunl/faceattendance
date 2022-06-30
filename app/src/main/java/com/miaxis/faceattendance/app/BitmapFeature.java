package com.miaxis.faceattendance.app;

public class BitmapFeature {
    public byte[] data;
    public int width;
    public int height;
    public boolean strict;
    public String mark;

    public BitmapFeature(byte[] data, int width, int height, boolean strict, String mark) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.strict = strict;
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "BitmapFeature{" +
                "data=" + data +
                ", width=" + width +
                ", height=" + height +
                ", strict=" + strict +
                ", mark='" + mark + '\'' +
                '}';
    }
}
