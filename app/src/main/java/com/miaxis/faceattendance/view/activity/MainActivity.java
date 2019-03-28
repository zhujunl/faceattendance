package com.miaxis.faceattendance.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.service.HttpCommServerService;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.fragment.AddPersonFragment;
import com.miaxis.faceattendance.view.fragment.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.fragment.PersonFragment;
import com.miaxis.faceattendance.view.fragment.RecordFragment;
import com.miaxis.faceattendance.view.fragment.SettingFragment;
import com.miaxis.faceattendance.view.fragment.VerifyFragment;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {

    @BindView(R.id.iv_drawer)
    ImageView ivDrawer;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.tv_verify)
    TextView tvVerify;
    @BindView(R.id.tv_person)
    TextView tvPerson;
    @BindView(R.id.tv_add_person)
    TextView tvAddPerson;
    @BindView(R.id.tv_record)
    TextView tvRecord;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.tv_quit)
    TextView tvQuit;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
    @BindView(R.id.tv_server_status)
    TextView tvServerStatus;
    @BindView(R.id.tv_server_ip)
    TextView tvServerIp;

    private MaterialDialog quitDialog;

    private HttpCommServerService httpCommServerService;

    private VerifyFragment verifyFragment;
    private PersonFragment personFragment;
    private AddPersonFragment addPersonFragment;
    private RecordFragment recordFragment;
    private SettingFragment settingFragment;

    public static Intent newInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        Intent intent = new Intent(this, HttpCommServerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void initView() {
        quitDialog = new MaterialDialog.Builder(this)
                .title("确认退出？")
                .positiveText("确认")
                .onPositive((dialog, which) -> finish())
                .negativeText("取消")
                .build();
        ivDrawer.setOnClickListener(v -> {
            if (dlMain.isDrawerOpen(GravityCompat.START)) {
                dlMain.closeDrawer(GravityCompat.START);
            } else {
                dlMain.openDrawer(GravityCompat.START);
            }
        });
        tvVerify.setOnClickListener(drawerClickListener);
        tvPerson.setOnClickListener(drawerClickListener);
        tvAddPerson.setOnClickListener(drawerClickListener);
        tvRecord.setOnClickListener(drawerClickListener);
        tvSetting.setOnClickListener(drawerClickListener);
        tvQuit.setOnClickListener(drawerClickListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, verifyFragment = VerifyFragment.newInstance()).commit();
    }

    @Override
    public void enterAnotherFragment(Class<? extends Fragment> removeClass, Class<? extends Fragment> addClass, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (TextUtils.equals(removeClass.getName(), VerifyFragment.class.getName())) {
            fragmentTransaction.detach(verifyFragment);
        } else if (TextUtils.equals(removeClass.getName(), PersonFragment.class.getName())) {
            fragmentTransaction.detach(personFragment);
        } else if (TextUtils.equals(removeClass.getName(), AddPersonFragment.class.getName())) {
            fragmentTransaction.detach(addPersonFragment);
        } else if (TextUtils.equals(removeClass.getName(), SettingFragment.class.getName())) {
            fragmentTransaction.detach(settingFragment);
        } else if (TextUtils.equals(removeClass.getName(), RecordFragment.class.getName())) {
            fragmentTransaction.detach(recordFragment);
        } else if (TextUtils.equals(removeClass.getName(), Fragment.class.getName())) {
            getSupportFragmentManager().popBackStackImmediate(null, 1);
        }
        if (TextUtils.equals(addClass.getName(), VerifyFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, verifyFragment = VerifyFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), PersonFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, personFragment = PersonFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), AddPersonFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, addPersonFragment = AddPersonFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), RecordFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, recordFragment = RecordFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), SettingFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, settingFragment = SettingFragment.newInstance());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        quitDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        System.exit(0);
    }

    private View.OnClickListener drawerClickListener = v -> {
        switch (v.getId()) {
            case R.id.tv_verify:
                enterAnotherFragment(Fragment.class, VerifyFragment.class, null);
                dlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_person:
                enterAnotherFragment(Fragment.class, PersonFragment.class, null);
                dlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_add_person:
                enterAnotherFragment(Fragment.class, AddPersonFragment.class, null);
                dlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_record:
                enterAnotherFragment(Fragment.class, RecordFragment.class, null);
                dlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_setting:
                enterAnotherFragment(Fragment.class, SettingFragment.class, null);
                dlMain.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_quit:
                onBackPressed();
                break;
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpCommServerService.MyBinder binder = (HttpCommServerService.MyBinder) service;
            httpCommServerService = binder.getService();
            httpCommServerService.setOnServerServiceListener(onServerServiceCallback);
            httpCommServerService.startServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private HttpCommServerService.OnServerServiceListener onServerServiceCallback = new HttpCommServerService.OnServerServiceListener() {
        @Override
        public void onServerStart(boolean result) {
            if (result) {
                tvServerStatus.setText("局域网服务在线中");
                tvServerIp.setText(ValueUtil.getIP(FaceAttendanceApp.getInstance()));
            } else {
                tvServerStatus.setText("局域网服务启动失败");
                tvServerIp.setText("");
            }
        }

        @Override
        public void onStartEditPerson() {
            runOnUiThread(() -> enterAnotherFragment(Fragment.class, PersonFragment.class, null));
        }

        @Override
        public boolean isPersonFragmentVisible() {
            return getVisibleFragment() instanceof PersonFragment;
        }

        @Override
        public void onDeletePerson(boolean start) {
            runOnUiThread(() -> {
                if (getVisibleFragment() instanceof PersonFragment) {
                    if (start) {
                        personFragment.showWaitDialogWithMessage("正在删除人员，请稍后");
                    } else {
                        personFragment.refreshPerson();
                        personFragment.dismissWaitDialog();
                        ToastManager.toast(MainActivity.this, "删除人员成功", ToastManager.SUCCESS);
                    }
                }
            });
        }

        @Override
        public void onClearPerson(boolean start) {
            runOnUiThread(() -> {
                if (getVisibleFragment() instanceof PersonFragment) {
                    if (start) {
                        personFragment.showWaitDialogWithMessage("正在清除人员，请稍后");
                    } else {
                        personFragment.refreshPerson();
                        personFragment.dismissWaitDialog();
                        ToastManager.toast(MainActivity.this, "清除人员成功", ToastManager.SUCCESS);
                    }
                }
            });
        }

        @Override
        public void onStopEditPerson() {
            runOnUiThread(() -> enterAnotherFragment(Fragment.class, VerifyFragment.class, null));
        }
    };

}
