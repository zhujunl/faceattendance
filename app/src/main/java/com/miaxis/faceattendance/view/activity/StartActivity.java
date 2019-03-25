package com.miaxis.faceattendance.view.activity;

import android.Manifest;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.event.InitFaceEvent;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StartActivity extends BaseActivity {

    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.tv_loading)
    TextView tvLoading;

    @Override
    protected int setContentView() {
        return R.layout.activity_start;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(aBoolean -> {
                    if (aBoolean) {
                        FaceAttendanceApp.getInstance().initApplicationAsync();
                    } else {
                        throw new Exception("拒绝权限");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                }, throwable -> Toast.makeText(StartActivity.this, "拒绝权限将无法正常使用", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void initView() {
        GlideApp.with(this).load(R.raw.loading).into(ivLoading);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onInitCWEvent(InitFaceEvent e) {
        switch (e.getResult()) {
            case InitFaceEvent.ERR_FILE_COMPARE:
                tvLoading.setText("文件校验失败");
                break;
            case InitFaceEvent.ERR_LICENCE:
                tvLoading.setText("读取授权文件失败");
                break;
            case InitFaceEvent.INIT_SUCCESS:
                tvLoading.setText("初始化算法成功");
                startActivity(MainActivity.newInstance(this));
                finish();
                break;
            default:
                tvLoading.setText("初始化算法失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
