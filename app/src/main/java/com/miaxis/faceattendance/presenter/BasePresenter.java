package com.miaxis.faceattendance.presenter;

import com.trello.rxlifecycle3.LifecycleProvider;

/**
 * Created by 一非 on 2018/4/9.
 */

public class BasePresenter<T> {
    private LifecycleProvider<T> provider;

    public BasePresenter(LifecycleProvider<T> provider) {
        this.provider = provider;
    }

    public LifecycleProvider<T> getProvider() {
        return provider;
    }
}
