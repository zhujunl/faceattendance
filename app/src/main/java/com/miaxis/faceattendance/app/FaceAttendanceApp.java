package com.miaxis.faceattendance.app;

import android.app.Application;

import com.miaxis.faceattendance.MyEventBusIndex;
import com.miaxis.faceattendance.event.InitFaceEvent;
import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.TTSManager;
import com.miaxis.faceattendance.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

public class FaceAttendanceApp extends Application {

    private static FaceAttendanceApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

    public static FaceAttendanceApp getInstance() {
        return instance;
    }

    public void initApplicationAsync() {
        FileUtil.initDirectory();
        DaoManager.getInstance().initDbHelper(getApplicationContext(), "FaceAttendance.db");
        TTSManager.getInstance().init(getApplicationContext());
        GpioManager.getInstance().init(this);
        int result = FaceManager.getInstance().initFaceST(getApplicationContext(), FileUtil.MODEL_PATH, FileUtil.LICENCE_PATH);
        EventBus.getDefault().postSticky(new InitFaceEvent(result));
    }

}
