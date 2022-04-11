package com.miaxis.faceattendance.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.app.GlideApp;
import com.miaxis.faceattendance.constant.Constants;
import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.event.DrawRectEvent;
import com.miaxis.faceattendance.event.FeatureEvent;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.manager.CardManager;
import com.miaxis.faceattendance.manager.CategoryManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.RecordManager;
import com.miaxis.faceattendance.manager.TTSManager;
import com.miaxis.faceattendance.manager.WhitelistManager;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.VerifyPerson;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;
import com.miaxis.faceattendance.view.listener.OnLimitClickHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Date;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class VerifyFragment extends BaseFragment {

    private static final int SET_HINT_MESSAGE = 1;
    private static final int SET_HINT_INVISIBLE = 2;
    private static final int DISMISS_VERIFY_PERSON = 3;
    private static final String MESSAGE_KEY = "message_key";

    @BindView(R.id.csv_camera)
    CameraSurfaceView csvCamera;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;
    //    @BindView(R.id.rv_verify)
//    RecyclerView rvVerify;
    @BindView(R.id.tv_open_verify)
    TextView tvOpenVerify;
    @BindView(R.id.iv_verify_frame)
    ImageView ivVerifyFrame;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_server_ip)
    TextView tvServerIp;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.ll_name)
    LinearLayout llName;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_category)
    TextView tvCategory;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;

    //    private VerifyAdapter<VerifyPerson> verifyAdapter;
    private OnFragmentInteractionListener mListener;
    private volatile boolean cardMode = false;
    private IDCardRecord idCardRecord;
    private FeatureEvent cameraFeatureData;
    private Handler handler = new MyHandler(this);
    private Person personCache;

    public static VerifyFragment newInstance() {
        return new VerifyFragment();
    }

    public VerifyFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return Constants.VERSION?R.layout.fragment_verify:R.layout.fragment_verify_860s;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        FaceManager.getInstance().clearVerifyList();
        FaceManager.getInstance().setIntervelTime(FaceManager.DEFAULT_INTERVEL_TIME);
        FaceManager.getInstance().setDelay(true);
        FaceManager.getInstance().startLoop();
    }

    @Override
    protected void initView() {
        tvServerIp.setText(ValueUtil.getIP(FaceAttendanceApp.getInstance()));
        tvVersion.setText(ValueUtil.getCurVersion(getContext()));
        tvOpenVerify.setOnClickListener(new OnLimitClickHelper(v -> {
            if (TextUtils.equals(tvOpenVerify.getText().toString(), "比对开关：开") && !cardMode) {
                FaceManager.getInstance().setVerify(false);
                tvOpenVerify.setText("比对开关：关");
                setSetHintInvisible();
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
        if (personCache == null || !TextUtils.equals(person.getCardNumber(), personCache.getCardNumber())) {
            personCache = person;
            VerifyPerson verifyPerson = new VerifyPerson.Builder()
                    .cardNumber(person.getCardNumber())
                    .name(person.getName())
                    .facePicturePath(person.getFacePicture())
                    .time(ValueUtil.simpleDateFormat.format(new Date()))
                    .build();
            tvName.setText(verifyPerson.getName());
            tvTime.setText(verifyPerson.getTime());
            llName.setVisibility(View.VISIBLE);
            llTime.setVisibility(View.VISIBLE);
            if (personCache.getCategoryId() != 0L) {
                llCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(CategoryManager.getInstance().getCategoryNameById(personCache.getCategoryId()));
            } else {
                llCategory.setVisibility(View.INVISIBLE);
            }
            GlideApp.with(getContext()).load(verifyPerson.getFacePicturePath()).into(ivHeader);
            Category category;
            if (person.getCategoryId() != 0
                    && (category = CategoryManager.getInstance().getCategoryById(person.getCategoryId())) != null) {
                TTSManager.getInstance().playVoiceMessageFlush(category.getCategoryPrompt());
            } else {
                TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getAttendancePrompt());
            }
            RecordManager.getInstance().saveRecord(event, verifyPerson.getTime());
            sendClearMessage();
        } else {
            setHintMessage("您 已 经 考 勤");
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDrawRectEvent(DrawRectEvent event) {
        if (event.getFaceNum() == 0) {
            if (!cardMode) {
                setSetHintInvisible();
            }
        } else if (event.getFaceNum() == -1) {
            setHintMessage("请 正 对 屏 幕");
        } else {
            setHintMessage("检 测 到 人 脸");
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCardEvent(CardEvent event) {
        switch (event.getMode()) {
            case CardEvent.FIND_CARD:
                GpioManager.getInstance().openLed();
                cardMode = true;
                setHintMessage("开 始 读 卡");
                FaceManager.getInstance().setIntervelTime(500);
                FaceManager.getInstance().setActiveVerify(false);
                FaceManager.getInstance().setVerify(true);
                break;
            case CardEvent.READ_CARD:
                if (cardMode) {
                    setHintMessage("读 卡 成 功");
                    idCardRecord = event.getIdCardRecord();
                    WhitelistManager.getInstance().checkWhitelist(idCardRecord.getCardNumber(), result -> {
                        if (idCardRecord != null && idCardRecord.getCardBitmap() != null) {
                            if (result) {
                                setHintMessage("白 名 单 校 验 通 过");
                                TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getWhitelistPrompt());
                                RecordManager.getInstance().uploadWhiteCardRecord(idCardRecord);
                            } else {
                                setHintMessage("开 始 人 证 核 验");
                                FaceManager.getInstance().getFeatureByBitmap(idCardRecord.getCardBitmap(), false, "card");
                            }
                        }
                    });
                }
                break;
            case CardEvent.NO_CARD:
                GpioManager.getInstance().closeLed();
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
                    setSetHintInvisible();
                }
                break;
            case CardEvent.OVERDUE:
                TTSManager.getInstance().playVoiceMessageFlush("已过期");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFeatureEvent(FeatureEvent event) {
        switch (event.getMode()) {
            case FeatureEvent.IMAGE_FACE:
                if (event!=null&&event.getFeature() != null) {
                    idCardRecord.setCardFeature(event.getFeature());
                } else {
                    TTSManager.getInstance().playVoiceMessageFlush("请重试");
                    setHintMessage("请 拿 开 证 件 重 试");
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
        CardManager.getInstance().setNeedReadCard(true);
        if (TextUtils.equals(tvOpenVerify.getText(), "比对开关：开")) {
            FaceManager.getInstance().setVerify(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FaceManager.getInstance().setVerify(false);
        FaceManager.getInstance().stopLoop();
        CardManager.getInstance().setNeedReadCard(false);
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

    static class MyHandler extends Handler {

        private WeakReference<VerifyFragment> fragmentWeakReference;

        MyHandler(VerifyFragment verifyFragment) {
            this.fragmentWeakReference = new WeakReference<>(verifyFragment);
        }

        @Override
        public synchronized void handleMessage(Message msg) {
            super.handleMessage(msg);
            VerifyFragment verifyFragment = fragmentWeakReference.get();
            if (verifyFragment != null) {
                try {
                    if (msg.what == SET_HINT_MESSAGE) {
                        String message = msg.getData().getString(MESSAGE_KEY);
                        verifyFragment.tvHint.setText(message);
                        verifyFragment.tvHint.setVisibility(View.VISIBLE);
                    } else if (msg.what == SET_HINT_INVISIBLE) {
                        verifyFragment.tvHint.setVisibility(View.INVISIBLE);
                    } else if (msg.what == DISMISS_VERIFY_PERSON) {
                        verifyFragment.personCache = null;
                        verifyFragment.dismissVerifyPerson();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dismissVerifyPerson() {
        GlideApp.with(getContext()).clear(ivHeader);
        llName.setVisibility(View.INVISIBLE);
        llTime.setVisibility(View.INVISIBLE);
        llCategory.setVisibility(View.INVISIBLE);
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
                    .subscribe(score -> {
                        Log.e("Verify:","人证Score"+score);
                        if (score > ConfigManager.getInstance().getConfig().getCardVerifyScore()) {
                            setHintMessage("核 验 成 功");
                            TTSManager.getInstance().playVoiceMessageFlush(ConfigManager.getInstance().getConfig().getCardVerifyPrompt());
                            RecordManager.getInstance().uploadCardRecord(idCardRecord, cameraEvent.getRgbImage(), score);
                        } else {
                            FaceManager.getInstance().setVerify(true);
                        }
                    }, throwable -> FaceManager.getInstance().setVerify(true));
        }
    }

    private void setHintMessage(final String message) {
        Message msg = handler.obtainMessage(SET_HINT_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_KEY, message);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void setSetHintInvisible() {
        Message msg = handler.obtainMessage(SET_HINT_INVISIBLE);
        handler.sendMessage(msg);
    }

    private void sendClearMessage() {
        handler.removeMessages(DISMISS_VERIFY_PERSON);
        Message msg = handler.obtainMessage(DISMISS_VERIFY_PERSON);
        handler.sendMessageDelayed(msg, 60 * 1000);
    }

}
