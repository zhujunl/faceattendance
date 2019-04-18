package com.miaxis.faceattendance.manager;

import com.annimon.stream.function.Consumer;
import com.miaxis.faceattendance.model.WhiteCardModel;
import com.miaxis.faceattendance.model.entity.WhiteCard;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WhitelistManager {

    private WhitelistManager() {
    }

    public static WhitelistManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final WhitelistManager instance = new WhitelistManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public void checkWhitelist(String cardNumber, Consumer<Boolean> consumer) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            WhiteCard whiteCard = WhiteCardModel.getWhiteCardByCardNumber(cardNumber);
            emitter.onNext(whiteCard != null);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer::accept,
                        throwable -> consumer.accept(Boolean.FALSE));
    }

}
