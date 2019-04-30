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

    public void checkWhitelist(String cardNumber, OnCheckWhitelistCallback callback) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            WhiteCard whiteCard = WhiteCardModel.getWhiteCardByCardNumber(cardNumber);
            emitter.onNext(whiteCard != null);
        })
                .subscribe(callback::onCheckWhitelist,
                        throwable -> callback.onCheckWhitelist(Boolean.FALSE));
    }

    public interface OnCheckWhitelistCallback {
        void onCheckWhitelist(boolean result);
    }

}
