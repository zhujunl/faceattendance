package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.constant.Constants;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Config;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import androidx.constraintlayout.widget.ConstraintLayout;
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
    @BindView(R.id.et_person_upload_url)
    EditText etPersonUploadUrl;
    @BindView(R.id.et_device_id)
    EditText etDeviceId;
    @BindView(R.id.et_Verify)
    EditText etVerify;
    @BindView(R.id.et_Quality)
    EditText etQuality;
    @BindView(R.id.et_Verify_Card)
    EditText etVerifyCard;
    @BindView(R.id.et_Quality_Card)
    EditText etQualityCard;
    @BindView(R.id.switch_Light)
    Switch swLight;
    @BindView(R.id.CL_Light)
    ConstraintLayout ClLight;

    private Config config;
    private OnFragmentInteractionListener mListener;

    private boolean light;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return Constants.VERSION? R.layout.fragment_setting:R.layout.fragment_setting_860s;
    }

    @Override
    protected void initData() {
        light=FaceAttendanceApp.getInstance().getSP().getBoolean("light",true);
        config = ConfigManager.getInstance().getConfig();
    }

    @Override
    protected void initView() {
        swLight.setChecked(light);
        tvVersion.setText(ValueUtil.getCurVersion(getContext()));
        etAttendanceUrl.setText(config.getUploadUrl());
        etCardVerifyUrl.setText(config.getCardUploadUrl());
        etPersonUploadUrl.setText(config.getPersonUploadUrl());
        etDeviceId.setText(config.getDeviceId());
        etPassword.setText(config.getPassword());
        etQuality.setText(String.valueOf(config.getVerifyQualityScore()));
        etVerify.setText(String.valueOf(config.getVerifyScore()));
        etVerifyCard.setText(String.valueOf(config.getCardVerifyScore()));
        etQualityCard.setText(String.valueOf(config.getRegisterQualityScore()));
        ivSaveConfig.setOnClickListener(v -> saveConfig());
        ClLight.setOnClickListener(v->{
            light=!light;
            swLight.setChecked(light);
        });
        swLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            light=isChecked;
        });
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
        if (!TextUtils.isEmpty(etPersonUploadUrl.getText().toString())) {
            if (!ValueUtil.isHttpFormat(etPersonUploadUrl.getText().toString())) {
                ToastManager.toast(getContext(), "人员新增上传数据URL校验失败，请输入\"http://host:port/api\"格式", ToastManager.INFO);
                return;
            }
        }
        if (TextUtils.isEmpty(etDeviceId.getText().toString())) {
            ToastManager.toast(getContext(), "设备标识不能为空", ToastManager.INFO);
            return;
        }
        if(TextUtils.isEmpty(etVerify.getText().toString())||TextUtils.isEmpty(etQuality.getText().toString())||
                TextUtils.isEmpty(etQualityCard.getText().toString())||TextUtils.isEmpty(etVerifyCard.getText().toString())){
            ToastManager.toast(getContext(), "比对阈值或质量阈值不能为空", ToastManager.INFO);
            return;
        }
        if (Float.parseFloat(etVerify.getText().toString().trim())<0||Float.parseFloat(etVerify.getText().toString().trim())>1){
            ToastManager.toast(getContext(), "人像比对阈值应在0~1之间", ToastManager.INFO);
            return;
        }
        if (Float.parseFloat(etQuality.getText().toString().trim())<0||Float.parseFloat(etQuality.getText().toString().trim())>100){
            ToastManager.toast(getContext(), "人像质量阈值应在0~100之间", ToastManager.INFO);
            return;
        }
        if (Float.parseFloat(etVerifyCard.getText().toString().trim())<0||Float.parseFloat(etVerifyCard.getText().toString().trim())>1){
            ToastManager.toast(getContext(), "人证比对阈值应在0~1之间", ToastManager.INFO);
            return;
        }
        if (Float.parseFloat(etQualityCard.getText().toString().trim())<0||Float.parseFloat(etQualityCard.getText().toString().trim())>100){
            ToastManager.toast(getContext(), "注册阈值应在0~100之间", ToastManager.INFO);
            return;
        }
        config.setUploadUrl(etAttendanceUrl.getText().toString());
        config.setCardUploadUrl(etCardVerifyUrl.getText().toString());
        config.setPersonUploadUrl(etPersonUploadUrl.getText().toString());
        config.setPassword(etPassword.getText().toString());
        config.setDeviceId(etDeviceId.getText().toString());
        config.setVerifyScore(Float.parseFloat(etVerify.getText().toString()));
        config.setVerifyQualityScore(Float.parseFloat(etQuality.getText().toString()));
        config.setRegisterQualityScore(Float.parseFloat(etQualityCard.getText().toString()));
        config.setCardVerifyScore(Float.parseFloat(etVerifyCard.getText().toString()));
        ConfigManager.getInstance().saveConfig(config, aBoolean -> {
            if (aBoolean) {
                SharedPreferences.Editor editor=FaceAttendanceApp.getInstance().getSP().edit();
                editor.putBoolean("light",light);
                editor.apply();
                ToastManager.toast(getContext(), "设置保存成功", ToastManager.SUCCESS);
            } else {
                ToastManager.toast(getContext(), "设置保存失败", ToastManager.ERROR);
            }
        });
    }

}
