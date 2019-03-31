package com.miaxis.faceattendance.presenter;

import com.miaxis.faceattendance.contract.RecordContract;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.entity.Record;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecordPresenter extends BasePresenter<FragmentEvent> implements RecordContract.Presenter {

    private RecordContract.View view;

    public RecordPresenter(LifecycleProvider<FragmentEvent> provider, RecordContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void loadRecord(int pageNum, int pageSize, String name, String sex, String cardNumber, Boolean upload, String startDate, String endDate) {
        Observable.create((ObservableOnSubscribe<List<Record>>)
                emitter -> emitter.onNext(RecordModel.queryRecord(pageNum, pageSize, name, sex, cardNumber, startDate, endDate, upload)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordList -> view.loadRecordCallback(recordList)
                        , throwable -> view.loadRecordCallback(new ArrayList<>()));
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }
}
