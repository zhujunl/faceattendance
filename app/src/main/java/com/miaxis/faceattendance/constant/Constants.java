package com.miaxis.faceattendance.constant;

import android.os.Build;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class Constants {

    public static final int TYPE_CAMERA = 0x11;//USB摄像头
    public static final int TYPE_ID_FP = 0x12;//指纹和⼆代证
    public static final int TYPE_LED_GREEN = 0x20;// LED 原MR990绿灯
    public static final int TYPE_LED_RED = 0x21;// LED 原MR990红灯
    public static final int TYPE_LED_BLUE = 0x22;// LED 原MR990蓝灯
    public static final int TYPE_LED = 0x23;// LED

    public static final String MOLD_POWER="com.miaxis.power";
    public static final String MOLD_STATUS="com.miaxis.status_bar";
    public static final String MOLD_NAV="com.miaxis.navigation";
    public static final String MOLD_INSTALL="com.miaxis.install";

    public static final boolean VERSION= !Build.VERSION.RELEASE.equals("11");//版本为Android11，VERSION为false
    public static final float pam=0.3f;//人脸图片截取扩大
    public static final long DIFFTIME=60000;//时间差
}
