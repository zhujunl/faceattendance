package com.miaxis.faceattendance.presenter;

import com.miaxis.faceattendance.contract.PersonContract;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.util.FileUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PersonPresenter extends BasePresenter<FragmentEvent> implements PersonContract.Presenter {

    private PersonContract.View view;

    public PersonPresenter(LifecycleProvider<FragmentEvent> provider, PersonContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void loadPerson(int pageNum, int pageSize) {
        Observable.create((ObservableOnSubscribe<List<Person>>)
                emitter -> emitter.onNext(PersonModel.loadPersonList(pageNum, pageSize)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(personList -> view.loadPersonCallback(personList)
                        , throwable -> view.loadPersonCallback(new ArrayList<>()));
    }

    @Override
    public void deletePerson(Person person) {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            PersonModel.deletePerson(person);
            emitter.onNext(0);
        })
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(i -> FileUtil.deletefile(person.getFacePicture()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> view.deletePersonCallback(person, true)
                        , throwable -> view.deletePersonCallback(null, false));
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }
}
