package com.miaxis.faceattendance.view.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.event.InitFaceEvent;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.service.HttpCommServerService;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.fragment.AddPersonFragment;
import com.miaxis.faceattendance.view.fragment.CategoryFragment;
import com.miaxis.faceattendance.view.fragment.PersonFragment;
import com.miaxis.faceattendance.view.fragment.RecordFragment;
import com.miaxis.faceattendance.view.fragment.SettingFragment;
import com.miaxis.faceattendance.view.fragment.UpdateFragment;
import com.miaxis.faceattendance.view.fragment.VerifyFragment;
import com.miaxis.faceattendance.view.fragment.WhitelistFragment;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;
import com.miaxis.faceattendance.view.listener.OnLimitClickListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.tv_whitelist)
    TextView tvWhitelist;
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
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.rl_init)
    RelativeLayout rlInit;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.tv_category)
    TextView tvCategory;

    private MaterialDialog passwordDialog;
    private MaterialDialog quitDialog;

    private HttpCommServerService httpCommServerService;

    private VerifyFragment verifyFragment;
    private PersonFragment personFragment;
    private AddPersonFragment addPersonFragment;
    private WhitelistFragment whitelistFragment;
    private RecordFragment recordFragment;
    private SettingFragment settingFragment;
    private UpdateFragment updateFragment;
    private CategoryFragment categoryFragment;

    private boolean stop = false;

    public static Intent newInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        rlToolbar.setVisibility(View.INVISIBLE);
        dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
                }, throwable -> Toast.makeText(MainActivity.this, "拒绝权限将无法正常使用", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void initView() {
        GlideApp.with(this).load(R.raw.loading).into(ivLoading);
        initDialog();
        ivDrawer.setOnClickListener(new OnLimitClickHelper(v -> {
            if (getVisibleFragment() instanceof VerifyFragment && !dlMain.isDrawerOpen(GravityCompat.START)) {
                passwordDialog.getInputEditText().setText("");
                passwordDialog.show();
            } else {
                changeDrawerState();
            }
        }));
        tvVerify.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvPerson.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvAddPerson.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvWhitelist.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvCategory.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvRecord.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvSetting.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
        tvQuit.setOnClickListener(new OnLimitClickHelper(drawerClickListener));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
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
                onInitFace();
                break;
            default:
                tvLoading.setText("初始化算法失败");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop = true;
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
        } else if (TextUtils.equals(removeClass.getName(), WhitelistFragment.class.getName())) {
            fragmentTransaction.detach(whitelistFragment);
        } else if (TextUtils.equals(removeClass.getName(), SettingFragment.class.getName())) {
            fragmentTransaction.detach(settingFragment);
        } else if (TextUtils.equals(removeClass.getName(), RecordFragment.class.getName())) {
            fragmentTransaction.detach(recordFragment);
        } else if (TextUtils.equals(removeClass.getName(), UpdateFragment.class.getName())) {
            fragmentTransaction.detach(updateFragment);
        } else if (TextUtils.equals(removeClass.getName(), CategoryFragment.class.getName())) {
            fragmentTransaction.detach(categoryFragment);
        } else if (TextUtils.equals(removeClass.getName(), Fragment.class.getName())) {
            getSupportFragmentManager().popBackStackImmediate(null, 1);
        }
        if (TextUtils.equals(addClass.getName(), VerifyFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, verifyFragment = VerifyFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), PersonFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, personFragment = PersonFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), AddPersonFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, addPersonFragment = AddPersonFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), WhitelistFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, whitelistFragment = WhitelistFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), RecordFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, recordFragment = RecordFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), SettingFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, settingFragment = SettingFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), CategoryFragment.class.getName())) {
            fragmentTransaction.replace(R.id.fl_main, categoryFragment = CategoryFragment.newInstance());
        } else if (TextUtils.equals(addClass.getName(), UpdateFragment.class.getName())) {
            if (bundle != null) {
                URL url = (URL) bundle.getSerializable("url");
                fragmentTransaction.replace(R.id.fl_main, updateFragment = UpdateFragment.newInstance(url));
            } else {
                ToastManager.toast(this, "发现错误数据，缺少Bundle", ToastManager.ERROR);
                enterAnotherFragment(Fragment.class, VerifyFragment.class, null);
                return;
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        ivDrawer.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (normal) {
            EventBus.getDefault().unregister(this);
            unbindService(serviceConnection);
            CardManager.getInstance().closeReadCard();
        }
    }

    private void onInitFace() {
        Intent intent = new Intent(this, HttpCommServerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        rlInit.setVisibility(View.GONE);
        rlToolbar.setVisibility(View.VISIBLE);
        CardManager.getInstance().startReadCard();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, verifyFragment = VerifyFragment.newInstance()).commit();
    }

    private OnLimitClickListener drawerClickListener = v -> {
        dlMain.closeDrawer(GravityCompat.START);
        switch (v.getId()) {
            case R.id.tv_verify:
                if (!(getVisibleFragment() instanceof VerifyFragment)) {
                    enterAnotherFragment(Fragment.class, VerifyFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                break;
            case R.id.tv_person:
                if (!(getVisibleFragment() instanceof PersonFragment)) {
                    enterAnotherFragment(Fragment.class, PersonFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_add_person:
                if (!(getVisibleFragment() instanceof AddPersonFragment)) {
                    enterAnotherFragment(Fragment.class, AddPersonFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_whitelist:
                if (!(getVisibleFragment() instanceof WhitelistFragment)) {
                    enterAnotherFragment(Fragment.class, WhitelistFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_category:
                if (!(getVisibleFragment() instanceof CategoryFragment)) {
                    enterAnotherFragment(Fragment.class, CategoryFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_record:
                if (!(getVisibleFragment() instanceof RecordFragment)) {
                    enterAnotherFragment(Fragment.class, RecordFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_setting:
                if (!(getVisibleFragment() instanceof SettingFragment)) {
                    enterAnotherFragment(Fragment.class, SettingFragment.class, null);
                    dlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                break;
            case R.id.tv_quit:
                quitDialog.show();
                break;
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("asd", "onServiceConnected");
            HttpCommServerService.MyBinder binder = (HttpCommServerService.MyBinder) service;
            httpCommServerService = binder.getService();
            httpCommServerService.setOnServerServiceListener(onServerServiceCallback);
            httpCommServerService.startServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("asd", "onServiceDisconnected");
            httpCommServerService.stopServer();
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
        public boolean onEnterFragment(Class<? extends Fragment> fragmentClass, Bundle bundle) {
            if (stop) {
                return false;
            }
            runOnUiThread(() -> {
                if (dlMain.isDrawerOpen(GravityCompat.START)) {
                    dlMain.closeDrawer(GravityCompat.START);
                }
                enterAnotherFragment(Fragment.class, fragmentClass, bundle);
            });
            return true;
        }

        @Override
        public boolean isPersonFragmentVisible() {
            return getVisibleFragment() instanceof PersonFragment;
        }

        @Override
        public boolean isAddPersonFragmentVisible() {
            return getVisibleFragment() instanceof AddPersonFragment;
        }

        @Override
        public void onBackstageBusy(boolean start, String message) {
            runOnUiThread(() -> {
                if (getVisibleFragment() instanceof PersonFragment) {
                    if (start) {
                        personFragment.showWaitDialogWithMessage(message);
                    } else {
                        personFragment.refreshPerson();
                        personFragment.dismissWaitDialog();
                        ToastManager.toast(MainActivity.this, message, ToastManager.INFO);
                    }
                }
            });
        }
    };

    private void changeDrawerState() {
        if (dlMain.isDrawerOpen(GravityCompat.START)) {
            dlMain.closeDrawer(GravityCompat.START);
        } else {
            dlMain.openDrawer(GravityCompat.START);
        }
    }

    private void initDialog() {
        passwordDialog = new MaterialDialog.Builder(this)
                .title("请输入设备密码")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("", "", (dialog, input) -> {})
                .inputRange(6, 6)
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    if (TextUtils.equals(dialog.getInputEditText().getText().toString(), ConfigManager.getInstance().getConfig().getPassword())) {
                        changeDrawerState();
                    } else {
                        ToastManager.toast(this, "密码错误", ToastManager.ERROR);
                    }
                })
                .negativeText("取消")
                .build();
        passwordDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        quitDialog = new MaterialDialog.Builder(this)
                .title("确认退出？")
                .positiveText("确认")
                .onPositive((dialog, which) -> finish())
                .negativeText("取消")
                .build();
    }

}
