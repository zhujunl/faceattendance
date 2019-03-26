package com.miaxis.faceattendance.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.miaxis.faceattendance.server.AttendanceServer;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class HttpCommServerService extends Service {

    private MyBinder myBinder = new MyBinder();
    private AttendanceServer attendanceServer;
    private OnServerServiceListener listener;

    public HttpCommServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    public class MyBinder extends Binder {
        public HttpCommServerService getService() {
            return HttpCommServerService.this;
        }
    }

    public interface OnServerServiceListener {
        void onServerStart(boolean result);
    }

    public void setOnServerServiceListener(OnServerServiceListener onServerServiceListener) {
        this.listener = onServerServiceListener;
    }

    public void startServer() {
        attendanceServer = new AttendanceServer(8081);
        try {
            attendanceServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            listener.onServerStart(true);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onServerStart(false);
            Log.e("asd", "Nano服务器开启失败");
        }
    }

    public void stopServer() {
        if (attendanceServer != null) {
            attendanceServer.stop();
        }
    }

}
