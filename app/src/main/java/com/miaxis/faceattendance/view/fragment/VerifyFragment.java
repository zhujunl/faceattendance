package com.miaxis.faceattendance.view.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.VerifyAdapter;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.event.DrawRectEvent;
import com.miaxis.faceattendance.event.FeatureEvent;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.RecordManager;
import com.miaxis.faceattendance.manager.TTSManager;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.manager.WhitelistManager;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.entity.VerifyPerson;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.custom.RectSurfaceView;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VerifyFragment extends BaseFragment {

    @BindView(R.id.csv_camera)
    CameraSurfaceView csvCamera;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;
    @BindView(R.id.rv_verify)
    RecyclerView rvVerify;
    @BindView(R.id.tv_open_verify)
    TextView tvOpenVerify;
    @BindView(R.id.iv_verify_frame)
    ImageView ivVerifyFrame;
    @BindView(R.id.tv_hint)
    TextView tvHint;

    private VerifyAdapter<VerifyPerson> verifyAdapter;
    private OnFragmentInteractionListener mListener;
    private boolean cardMode = false;
    private IDCardRecord idCardRecord;
    private FeatureEvent cameraFeatureData;

    public static VerifyFragment newInstance() {
        return new VerifyFragment();
    }

    public VerifyFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_verify;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        FaceManager.getInstance().clearVerifyList();
        FaceManager.getInstance().setIntervelTime(FaceManager.DEFAULT_INTERVEL_TIME);
        FaceManager.getInstance().setDelay(true);
    }

    @Override
    protected void initView() {
        verifyAdapter = new VerifyAdapter<>(getContext(), new ArrayList<>());
        rvVerify.setAdapter(verifyAdapter);
        rvVerify.setLayoutManager(new LinearLayoutManager(getContext()));
        tvOpenVerify.setOnClickListener(new OnLimitClickHelper(v -> {
            if (TextUtils.equals(tvOpenVerify.getText().toString(), "比对开关：开") && !cardMode) {
                FaceManager.getInstance().setVerify(false);
                tvOpenVerify.setText("比对开关：关");
                verifyAdapter.setDataList(new ArrayList<>());
                verifyAdapter.notifyDataSetChanged();
                tvHint.setVisibility(View.INVISIBLE);
            } else if (TextUtils.equals(tvOpenVerify.getText().toString(), "比对开关：关") && !cardMode) {
                tvOpenVerify.setText("比对开关：开");
                FaceManager.getInstance().setVerify(true);
            }
        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenCameraEvent(OpenCameraEvent event) {
        int rootWidth = rlRoot.getWidth();
        int rootHeight = rootWidth * event.getPreviewHeight() / event.getPreviewWidth();
//        int rootHeight = (int) (ValueUtil.getScreenHeight(getContext()) - ValueUtil.getStateBarHeight(getContext()) - getContext().getResources().getDimension(R.dimen.custom_toolbar_height));
//        int rootWidth = rootHeight * event.getPreviewWidth() / event.getPreviewHeight();
        resetLayoutParams(rlRoot, rootWidth, rootHeight);
        resetLayoutParams(csvCamera, rootWidth, rootHeight);
        resetLayoutParams(ivVerifyFrame, rootWidth, rootHeight);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyPersonEvent(VerifyPersonEvent event) {
        Person person = event.getPerson();
        if (!verifyAdapter.containsName(person.getCardNumber())) {
            VerifyPerson verifyPerson = new VerifyPerson.Builder()
                    .cardNumber(person.getCardNumber())
                    .name(person.getName())
                    .facePicturePath(person.getFacePicture())
                    .time(ValueUtil.simpleDateFormat.format(new Date()))
                    .build();
            verifyAdapter.insertData(0, verifyPerson);
            rvVerify.scrollToPosition(0);
            TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getAttendancePrompt());
            RecordManager.getInstance().saveRecord(event, verifyPerson.getTime());
        } else {
            tvHint.setText("您 已 经 考 勤");
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawRectEvent(DrawRectEvent event) {
        if (event.getFaceNum() == 0) {
            if (!cardMode) {
                tvHint.setVisibility(View.INVISIBLE);
            }
        } else if (event.getFaceNum() == -1) {
            tvHint.setText("请 正 对 屏 幕");
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setText("检 测 到 人 脸");
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardEvent(CardEvent event) {
        switch (event.getMode()) {
            case CardEvent.FIND_CARD:
                cardMode = true;
                tvHint.setText("开 始 读 卡");
                tvHint.setVisibility(View.VISIBLE);
                FaceManager.getInstance().setIntervelTime(500);
                FaceManager.getInstance().setActiveVerify(false);
                FaceManager.getInstance().setVerify(true);
                break;
            case CardEvent.READ_CARD:
                if (cardMode) {
                    tvHint.setText("读 卡 成 功");
                    idCardRecord = event.getIdCardRecord();
                    WhitelistManager.getInstance().checkWhitelist(idCardRecord.getCardNumber(), result -> {
                        if (result) {
                            tvHint.setText("白 名 单 校 验 通 过");
                            TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getWhitelistPrompt());
                            RecordManager.getInstance().uploadWhiteCardRecord(idCardRecord);
                        } else {
                            tvHint.setText("开 始 人 证 核 验");
                            FaceManager.getInstance().getFeatureByBitmap(idCardRecord.getCardBitmap());
                        }
                    });
                }
                break;
            case CardEvent.NO_CARD:
                if (cardMode) {
                    cardMode = false;
                    FaceManager.getInstance().setActiveVerify(true);
                    FaceManager.getInstance().setIntervelTime(FaceManager.DEFAULT_INTERVEL_TIME);
                    if (TextUtils.equals(tvOpenVerify.getText(), "比对开关：开")) {
                        FaceManager.getInstance().setVerify(true);
                    } else {
                        FaceManager.getInstance().setVerify(false);
                    }
                    idCardRecord = null;
                    cameraFeatureData = null;
                    tvHint.setVisibility(View.INVISIBLE);
                }
                break;
            case CardEvent.OVERDUE:
                TTSManager.getInstance().playVoiceMessageFlush("已过期");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeatureEvent(FeatureEvent event) {
        switch (event.getMode()) {
            case FeatureEvent.IMAGE_FACE:
                if (event.getFeature() != null) {
                    idCardRecord.setCardFeature(event.getFeature());
                } else {
                    ToastManager.toast(getContext(), "证件照片提取特征失败：" + event.getMessage(), ToastManager.INFO);
                    tvHint.setText("请 拿 开 证 件 重 试");
                }
                break;
            case FeatureEvent.CAMERA_FACE:
                if (event.getFeature() != null && event.getRgbImage() != null) {
                    FaceManager.getInstance().setVerify(false);
                    cameraFeatureData = event;
                }
                break;
        }
        witnessVerification(idCardRecord, cameraFeatureData);
    }

    @Override
    public void onResume() {
        super.onResume();
        CardManager.getInstance().startReadCard();
        if (TextUtils.equals(tvOpenVerify.getText(), "比对开关：开")) {
            FaceManager.getInstance().setVerify(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FaceManager.getInstance().setVerify(false);
        CardManager.getInstance().closeReadCard();
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
        EventBus.getDefault().unregister(this);
        FaceManager.getInstance().setVerify(false);
    }

    private void resetLayoutParams(View view, int fixWidth, int fixHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = fixWidth;
        layoutParams.height = fixHeight;
        view.setLayoutParams(layoutParams);
    }

    private void witnessVerification(IDCardRecord idCardRecord, FeatureEvent cameraEvent) {
        if (idCardRecord != null && idCardRecord.getCardFeature() != null && cameraEvent != null) {
            Observable.create((ObservableOnSubscribe<Float>) emitter -> {
                float score = FaceManager.getInstance().matchFeature(idCardRecord.getCardFeature(), cameraEvent.getFeature());
                emitter.onNext(score);
            })
                    .subscribeOn(Schedulers.io())
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(score -> {
                        if (score > ConfigManager.getInstance().getConfig().getVerifyScore()) {
                            tvHint.setText("核 验 成 功");
                            TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getCardVerifyPrompt());
                            RecordManager.getInstance().uploadCardRecord(idCardRecord, cameraEvent.getRgbImage(), score);
                        } else {
                            FaceManager.getInstance().setVerify(true);
                        }
                    }, throwable -> FaceManager.getInstance().setVerify(true));
        }
    }

}
