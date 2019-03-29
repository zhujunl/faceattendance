package com.miaxis.faceattendance.view.fragment;


import android.content.Context;
import android.widget.ImageView;

import com.miaxis.faceattendance.R;
import com.miaxis.faceattendance.adapter.RecordAdapter;
import com.miaxis.faceattendance.adapter.listener.EndLessOnScrollListener;
import com.miaxis.faceattendance.contract.RecordContract;
import com.miaxis.faceattendance.manager.ToastManager;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.presenter.RecordPresenter;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.listener.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends BaseFragment implements RecordContract.View {

    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;

    private OnFragmentInteractionListener mListener;
    private RecordContract.Presenter presenter;
    private RecordAdapter<Record> recordAdapter;
    private EndLessOnScrollListener endLessOnScrollListener;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initData() {
        presenter = new RecordPresenter(this, this);
    }

    @Override
    protected void initView() {
        recordAdapter = new RecordAdapter<>(getContext(), new ArrayList<>());
        rvRecord.setAdapter(recordAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecord.setLayoutManager(linearLayoutManager);
        rvRecord.setItemAnimator(new SlideInLeftAnimator());
        endLessOnScrollListener = new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.loadRecord(currentPage, ValueUtil.PAGESIZE);
            }
        };
        rvRecord.addOnScrollListener(endLessOnScrollListener);
        recordAdapter.setOnItemClickListener((view, position) -> {
        });
        presenter.loadRecord(0, ValueUtil.PAGESIZE);
    }

    @Override
    public void loadRecordCallback(List<Record> recordList) {
        if (recordList != null) {
            if (!recordList.isEmpty()) {
                recordAdapter.appendDataList(recordList);
                recordAdapter.notifyDataSetChanged();
            } else {
                ToastManager.toast(getContext(), "没有更多了", ToastManager.INFO);
            }
        } else {
            ToastManager.toast(getContext(), "读取数据库失败", ToastManager.ERROR);
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
        presenter.doDestroy();
    }

}
