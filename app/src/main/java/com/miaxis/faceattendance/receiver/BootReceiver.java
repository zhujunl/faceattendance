package com.miaxis.faceattendance.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.miaxis.faceattendance.view.activity.StartActivity;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent sayHelloIntent = new Intent(context, StartActivity.class);
        sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sayHelloIntent);
    }
}
