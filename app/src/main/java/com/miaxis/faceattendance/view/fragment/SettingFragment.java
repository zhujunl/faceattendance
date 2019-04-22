package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Config;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import androidx.fragment.app.Fragment;
import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {

    @BindView(R.id.iv_save_config)
    ImageView ivSaveConfig;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.et_attendance_url)
    EditText etAttendanceUrl;
    @BindView(R.id.et_card_verify_url)
    EditText etCardVerifyUrl;
    @BindView(R.id.et_password)
    EditText etPassword;

    private Config config;
    private OnFragmentInteractionListener mListener;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initData() {
        config = ConfigManager.getInstance().getConfig();
    }

    @Override
    protected void initView() {
        tvVersion.setText(ValueUtil.getCurVersion(getContext()));
        etAttendanceUrl.setText(config.getUploadUrl());
        etCardVerifyUrl.setText(config.getCardUploadUrl());
        etPassword.setText(config.getPassword());
        ivSaveConfig.setOnClickListener(v -> saveConfig());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void saveConfig() {
        if (!TextUtils.isEmpty(etAttendanceUrl.getText().toString())) {
            if (!ValueUtil.isHttpFormat(etAttendanceUrl.getText().toString())) {
                ToastManager.toast(getContext(), "人脸考勤上传数据URL校验失败，请输入\"http://host:port/api\"格式", ToastManager.INFO);
                return;
            }
        }
        if (!TextUtils.isEmpty(etCardVerifyUrl.getText().toString())) {
            if (!ValueUtil.isHttpFormat(etCardVerifyUrl.getText().toString())) {
                ToastManager.toast(getContext(), "人证核验上传数据URL校验失败，请输入\"http://host:port/api\"格式", ToastManager.INFO);
                return;
            }
        }
        config.setUploadUrl(etAttendanceUrl.getText().toString());
        config.setCardUploadUrl(etCardVerifyUrl.getText().toString());
        config.setPassword(etPassword.getText().toString());
        ConfigManager.getInstance().saveConfig(config, aBoolean -> {
            if (aBoolean) {
                ToastManager.toast(getContext(), "设置保存成功", ToastManager.SUCCESS);
            } else {
                ToastManager.toast(getContext(), "设置保存失败", ToastManager.ERROR);
            }
        });
    }

}
