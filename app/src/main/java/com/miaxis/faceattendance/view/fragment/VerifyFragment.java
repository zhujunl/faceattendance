package com.miaxis.faceattendance.view.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.event.OpenCameraEvent;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.view.custom.CameraSurfaceView;
import com.miaxis.faceattendance.view.custom.RectSurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class VerifyFragment extends BaseFragment {

    @BindView(R.id.csv_camera)
    CameraSurfaceView csvCamera;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsvRect;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    @BindView(R.id.ll_panel)
    LinearLayout llPanel;

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
    }

    @Override
    protected void initView() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenCameraEvent(OpenCameraEvent event) {
        int rootWidth = flRoot.getWidth();
        int rootHeight = rootWidth * event.getPreviewHeight() / event.getPreviewWidth();
        resetLayoutParams(flRoot, rootWidth, rootHeight);
        resetLayoutParams(csvCamera, rootWidth, rootHeight);
        resetLayoutParams(rsvRect, rootWidth, rootHeight);
        rsvRect.setZoomRate((float) event.getPreviewHeight() / FaceManager.ZOOM_WIDTH);
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
    }

    private void resetLayoutParams(View view, int fixWidth, int fixHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = fixWidth;
        layoutParams.height = fixHeight;
        view.setLayoutParams(layoutParams);
    }

}
