package com.miaxis.faceattendance.view.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.faceattendance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends BaseFragment {

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

    }

    @Override
    protected void initView() {

    }
}
