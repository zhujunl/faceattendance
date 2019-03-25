package com.miaxis.faceattendance.manager;

import android.util.Log;

import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.util.FileUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecordManager {

    private RecordManager() {
    }

    public static RecordManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final RecordManager instance = new RecordManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public void saveRecord(VerifyPersonEvent event, String time) {
        Observable.create((ObservableOnSubscribe<Record>) emitter -> {
            Person person = event.getPerson();
            Record record = new Record.Builder()
                    .cardNumber(person.getCardNumber())
                    .latitude(String.valueOf(AmapManager.getInstance().getaMapLocation().getLatitude()))
                    .longitude(String.valueOf(AmapManager.getInstance().getaMapLocation().getLongitude()))
                    .location(AmapManager.getInstance().getaMapLocation().getAddress())
                    .name(person.getName())
                    .result("人脸通过")
                    .verifyTime(time)
                    .score(String.valueOf(event.getScore()))
                    .build();
            emitter.onNext(record);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(record -> {
                    RGBImage rgbImage = event.getRgbImage();
                    String imagePath = FileUtil.IMG_PATH + File.separator + record.getName() + System.currentTimeMillis() + ".jpg";
                    FaceManager.getInstance().saveRGBImageData(imagePath, rgbImage.getRgbImage(), rgbImage.getWidth(), rgbImage.getHeight());
                    record.setFacePicture(imagePath);
                    RecordModel.saveRecord(record);
                }, throwable -> Log.e("asd", "RecordManager::saveRecord" + throwable.getMessage()));

    }

}
