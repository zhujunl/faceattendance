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
import com.miaxis.faceattendance.event.DrawRectEvent;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.GpioManager;
import com.miaxis.faceattendance.manager.RecordManager;
import com.miaxis.faceattendance.model.entity.Person;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

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
        FaceManager.getInstance().setVerify(true);
        FaceManager.getInstance().setDelay(true);
    }

    @Override
    protected void initView() {
        verifyAdapter = new VerifyAdapter<>(getContext(), new ArrayList<>());
        rvVerify.setAdapter(verifyAdapter);
        rvVerify.setLayoutManager(new LinearLayoutManager(getContext()));
        tvOpenVerify.setOnClickListener(new OnLimitClickHelper(v -> {
            if (TextUtils.equals(tvOpenVerify.getText().toString(), "比对开关：开")) {
                FaceManager.getInstance().setVerify(false);
                tvOpenVerify.setText("比对开关：关");
                verifyAdapter.setDataList(new ArrayList<>());
                verifyAdapter.notifyDataSetChanged();
            } else if (TextUtils.equals(tvOpenVerify.getText().toString(), "比对开关：关")) {
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
            RecordManager.getInstance().saveRecord(event, verifyPerson.getTime());
        } else {
            tvHint.setText("您 已 经 考 勤");
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawRectEvent(DrawRectEvent event) {
        if (event.getFaceNum() == 0) {
            tvHint.setVisibility(View.INVISIBLE);
        } else if (event.getFaceNum() == -1) {
            tvHint.setText("请 正 对 屏 幕");
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setText("检 测 到 人 脸");
            tvHint.setVisibility(View.VISIBLE);
        }
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

}
