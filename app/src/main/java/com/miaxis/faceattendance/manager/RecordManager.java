package com.miaxis.faceattendance.manager;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.model.net.UpLoadRecord;
import com.miaxis.faceattendance.util.FileUtil;

import java.io.File;
import java.net.URL;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Url;

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
                    .sex(person.getSex())
                    .verifyTime(time)
                    .score(String.valueOf(event.getScore()))
                    .upload(Boolean.FALSE)
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
                    uploadRecord(record);
                }, throwable -> Log.e("asd", "RecordManager::saveRecord" + throwable.getMessage()));
    }

    private void uploadRecord(Record record) {
        String uploadUrl = ConfigManager.getInstance().getConfig().getUploadUrl();
        if (!TextUtils.isEmpty(uploadUrl)) {
            Observable.create((ObservableOnSubscribe<Retrofit>) emitter -> {
                URL url = new URL(uploadUrl);
                emitter.onNext(FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build());
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<Retrofit, ObservableSource<ResponseEntity>>) retrofit -> {
                        record.setFacePicture(FileUtil.pathToBase64(record.getFacePicture()));
                        UpLoadRecord upLoadRecord = retrofit.create(UpLoadRecord.class);
                        String json = new Gson().toJson(record);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecord.uploadData(uploadUrl, requestBody);
                    })
                    .subscribe(responseEntity -> {
                        record.setUpload(Boolean.TRUE);
                        RecordModel.updateRecord(record);
                    }, Throwable::printStackTrace);
        }
    }

}
