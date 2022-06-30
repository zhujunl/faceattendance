package com.miaxis.faceattendance.manager;

import android.app.Application;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.constant.Constants;

public class GpioManager {

    private GpioManager() {
    }

    public static GpioManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final GpioManager instance = new GpioManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static final int GPIO_INTERVAL = 100;

    private SmdtManager smdtManager;
    private boolean humanInductionFlag = true;

    public void init(Application application) {
        smdtManager = new SmdtManager(application);
        initGPIO();
    }

    /**
     * 摄像头上电
     */
    private void initGPIO() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                smdtManager.smdtSetGpioDirection(1, 0);         // value  0 读 1 写
                Thread.sleep(GPIO_INTERVAL);
                smdtManager.smdtSetGpioDirection(2, 1);
                Thread.sleep(GPIO_INTERVAL);
            }
            for (int i = 0; i < 3; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, true);
                if (re == 0) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetCameraGpio() {
        if(!Constants.VERSION){
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,false);
            SystemClock.sleep(800);
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA,true);
            return;
        }
        smdtManager.smdtSetGpioValue(2, false);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        smdtManager.smdtSetGpioValue(2, true);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*
    * 摄像头上电*/
    public void openCameraGpio() {
        if(!Constants.VERSION){
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA,true);
            return;
        }
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, false);
                if (re == 0) {
                    break;
                }
            }
            Thread.sleep(GPIO_INTERVAL);
            for (int i = 0; i < 3; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, true);
                if (re == 0) {
                    break;
                }
            }
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
     * 摄像头下电*/
    public void closeCameraGpio() {
        if(!Constants.VERSION){
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,false);
            return;
        }
        try {
            for (int i = 0; i < 3; i++) {
                int result = smdtManager.smdtSetGpioValue(2, false);
                if (result == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开闪光灯
     */
    public void openLed() {
        if (!FaceAttendanceApp.getInstance().getSP().getBoolean("light",true)){
            return;
        }
        if(!Constants.VERSION){
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,true);
            return;
        }
        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭闪光灯
     */
    public void closeLed() {
        if (!FaceAttendanceApp.getInstance().getSP().getBoolean("light",true)){
            return;
        }
        if(!Constants.VERSION){
            FaceAttendanceApp.getInstance().sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_LED,false);
            return;
        }
        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSmdtStatusBar(Context context, boolean value) {
        try {
            smdtManager.smdtSetStatusBar(context, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 线程 人体感应线程 */
    public class HumanInductionThread extends Thread {
        @Override
        public void run() {
            while (humanInductionFlag) {
                try {
                    Thread.sleep(GPIO_INTERVAL);
                    if (smdtManager.smdtReadGpioValue(1) == 1) {
                        openLed();
                    } else {
                        closeLed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
