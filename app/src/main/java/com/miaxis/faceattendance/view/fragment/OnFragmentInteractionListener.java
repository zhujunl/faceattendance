package com.miaxis.faceattendance.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface OnFragmentInteractionListener {
    void enterAnotherFragment(Class<? extends Fragment> removeClass, Class<? extends Fragment> addClass, Bundle bundle);
}
