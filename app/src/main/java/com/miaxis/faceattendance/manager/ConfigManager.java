package com.miaxis.faceattendance.manager;

import com.annimon.stream.function.Consumer;
import com.miaxis.faceattendance.model.ConfigModel;
import com.miaxis.faceattendance.model.entity.Config;
import com.miaxis.faceattendance.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigManager {

    private ConfigManager() {}

    public static ConfigManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ConfigManager instance = new ConfigManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void checkConfig() {
        config = DaoManager.getInstance().getDaoSession().getConfigDao().load(1L);
        if (config == null) {
            config = new Config.Builder()
                    .id(1L)
                    .uploadUrl(ValueUtil.DEFAULT_UPLOAD_URL)
                    .cardUploadUrl(ValueUtil.DEFAULT_CARD_UPLOAD_URL)
                    .password(ValueUtil.DEFAULT_PASSWORD)
                    .qualityScore(ValueUtil.DEFAULT_QUALITY_SCORE)
                    .verifyScore(ValueUtil.DEFAULT_VERIFY_SCORE)
                    .cardVerifyScore(ValueUtil.DEFAULT_CARD_VERIFY_SCORE)
                    .recordClearThreshold(ValueUtil.DEFAULT_RECORD_CLEAR_THRESHOLD)
                    .build();
            DaoManager.getInstance().getDaoSession().getConfigDao().insert(config);
        }
    }

    public void saveConfig(Config config, Consumer<Boolean> consumer) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ConfigModel.saveConfig(config);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer::accept, throwable -> consumer.accept(Boolean.FALSE));
    }

}
