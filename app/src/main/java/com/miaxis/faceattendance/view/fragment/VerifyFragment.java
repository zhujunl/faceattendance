package com.miaxis.faceattendance.view.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.VerifyAdapter;
import com.miaxis.faceattendance.event.DrawRectEvent;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.manager.RecordManager;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.VerifyPerson;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.custom.RectSurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class VerifyFragment extends BaseFragment {

    @BindView(R.id.csv_camera)
    CameraSurfaceView csvCamera;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsvRect;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    @BindView(R.id.rv_verify)
    RecyclerView rvVerify;

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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenCameraEvent(OpenCameraEvent event) {
        int rootWidth = flRoot.getWidth();
        int rootHeight = rootWidth * event.getPreviewHeight() / event.getPreviewWidth();
        resetLayoutParams(flRoot, rootWidth, rootHeight);
        resetLayoutParams(csvCamera, rootWidth, rootHeight);
        resetLayoutParams(rsvRect, rootWidth, rootHeight);
        rsvRect.setRootSize(rootWidth, rootHeight);
        rsvRect.setZoomRate((float) rootWidth / FaceManager.ZOOM_WIDTH);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawRectEvent(DrawRectEvent event) {
        rsvRect.drawRect(event.getFaceInfos(), event.getFaceNum());
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
