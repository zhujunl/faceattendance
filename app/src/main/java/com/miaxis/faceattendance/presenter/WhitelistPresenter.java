package com.miaxis.faceattendance.presenter;

import com.miaxis.faceattendance.contract.WhitelistContract;
import com.miaxis.faceattendance.model.WhiteCardModel;
import com.miaxis.faceattendance.model.entity.WhiteCard;
import com.miaxis.faceattendance.util.ValueUtil;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WhitelistPresenter extends BasePresenter<FragmentEvent> implements WhitelistContract.Presenter {

    private WhitelistContract.View view;

    public WhitelistPresenter(LifecycleProvider provider, WhitelistContract.View view) {
        super(provider);
        this.view = view;
    }

    @Override
    public void loadWhitelist(int pageNum, int pageSize) {
        Observable.create((ObservableOnSubscribe<List<WhiteCard>>) emitter ->
                emitter.onNext(WhiteCardModel.loadWhitelist(pageNum, pageSize)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCardList -> view.loadWhitelistCallback(whiteCardList),
                        throwable -> view.loadWhitelistCallback(null));
    }

    @Override
    public void addWhiteCard(String name, String cardNumber) {
        Observable.create((ObservableOnSubscribe<WhiteCard>) emitter -> {
            WhiteCard whiteCard = new WhiteCard(cardNumber, name, ValueUtil.simpleDateFormat.format(new Date()));
            emitter.onNext(whiteCard);
        })
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(Schedulers.io())
                .doOnNext(WhiteCardModel::saveWhiteCard)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCard -> view.addWhiteCardCallback(Boolean.TRUE),
                        throwable -> view.addWhiteCardCallback(Boolean.FALSE));
    }

    @Override
    public void deleteWhiteCard(WhiteCard whiteCard) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            WhiteCardModel.deleteWhileCard(whiteCard);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> view.deleteWhiteCardCallback(whiteCard, aBoolean),
                        throwable -> view.deleteWhiteCardCallback(null, Boolean.FALSE));
    }

    @Override
    public void getWhiteCardByCardNumber(String cardNumber) {
        Observable.create((ObservableOnSubscribe<WhiteCard>) emitter ->
                emitter.onNext(WhiteCardModel.getWhiteCardByCardNumber(cardNumber)))
                .subscribeOn(Schedulers.io())
                .compose(getProvider().bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(whiteCard -> view.getWhiteCardByCardNumberCallback(whiteCard),
                        throwable -> view.getWhiteCardByCardNumberCallback(null));
    }

    @Override
    public void doDestroy() {
        this.view = null;
    }
}
