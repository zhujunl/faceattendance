package com.miaxis.faceattendance.app;

import android.app.Application;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.faceattendance.MyEventBusIndex;
import com.miaxis.faceattendance.event.InitFaceEvent;
import com.miaxis.faceattendance.manager.AmapManager;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.CategoryManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.CrashExceptionManager;
import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.TTSManager;
import com.miaxis.faceattendance.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaceAttendanceApp extends Application {

    private static FaceAttendanceApp instance;

//    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().
//            connectTimeout(5, TimeUnit.SECONDS).
//            readTimeout(5, TimeUnit.SECONDS).
//            writeTimeout(5, TimeUnit.SECONDS).build();

    public static final Retrofit.Builder RETROFIT = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

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
        FileDownloader.setup(this);
        DaoManager.getInstance().initDbHelper(getApplicationContext(), "FaceAttendance.db");
        TTSManager.getInstance().init(getApplicationContext());
        AmapManager.getInstance().startLocation(this);
        GpioManager.getInstance().init(this);
        ConfigManager.getInstance().checkConfig();
        CategoryManager.getInstance().checkCategory();
        CrashExceptionManager.getInstance().init(this);
        CardManager.getInstance().init(this);
        int faceResult = FaceManager.getInstance().initFaceST(getApplicationContext(), FileUtil.MODEL_PATH, FileUtil.LICENCE_PATH);
        EventBus.getDefault().post(new InitFaceEvent(faceResult));
    }

}
