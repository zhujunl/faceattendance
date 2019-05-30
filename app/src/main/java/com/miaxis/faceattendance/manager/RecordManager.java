package com.miaxis.faceattendance.manager;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.event.VerifyPersonEvent;
import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.entity.CardRecord;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.entity.UploadPerson;
import com.miaxis.faceattendance.model.entity.UploadRecord;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.model.net.UpLoadRecordNet;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    private Subscription subscription;

    public void saveRecord(VerifyPersonEvent event, String time) {
        Observable.create((ObservableOnSubscribe<Record>) emitter -> {
            Person person = event.getPerson();
            String latitude = "";
            String longitude = "";
            String location = "";
            if (AmapManager.getInstance().getaMapLocation() != null) {
                latitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLatitude());
                longitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLongitude());
                location = AmapManager.getInstance().getaMapLocation().getAddress();
            }
            Record record = new Record.Builder()
                    .cardNumber(person.getCardNumber())
                    .latitude(latitude)
                    .longitude(longitude)
                    .location(location)
                    .name(person.getName())
                    .sex(person.getSex())
                    .verifyTime(time)
                    .score(String.valueOf(event.getScore()))
                    .upload(Boolean.FALSE)
                    .categoryId(person.getCategoryId())
                    .build();
            emitter.onNext(record);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(record -> {
                    RGBImage rgbImage = event.getRgbImage();
                    String imagePath = FileUtil.IMG_PATH + File.separator + record.getCardNumber() + "-" + System.currentTimeMillis() + ".jpg";
                    FaceManager.getInstance().saveRGBImageData(imagePath, rgbImage.getRgbImage(), rgbImage.getWidth(), rgbImage.getHeight());
                    record.setFacePicture(imagePath);
                    RecordModel.saveRecord(record);
                    new Thread(() -> {
                        try {
                            uploadAllNotUploadedRecord();
                        } catch (MalformedURLException e) {
                            Log.e("asd", "上传日志外围：" + e.getMessage());
                            e.printStackTrace();
                        }
                    }).start();
                }, Throwable::printStackTrace);
    }

    public void uploadCardRecord(IDCardRecord idCardRecord, RGBImage rgbImage, float score) {
        String uploadUrl = ConfigManager.getInstance().getConfig().getCardUploadUrl();
        if (!TextUtils.isEmpty(uploadUrl)) {
            Observable.create((ObservableOnSubscribe<CardRecord>) emitter -> {
                String latitude = "";
                String longitude = "";
                String location = "";
                if (AmapManager.getInstance().getaMapLocation() != null) {
                    latitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLatitude());
                    longitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLongitude());
                    location = AmapManager.getInstance().getaMapLocation().getAddress();
                }
                byte[] cameraImageData = FaceManager.getInstance().imageEncode(rgbImage.getRgbImage(), rgbImage.getWidth(), rgbImage.getHeight());
                String facePicture = cameraImageData != null ? Base64.encodeToString(cameraImageData, Base64.NO_WRAP) : "";
                String cardPicture = FileUtil.bitmapToBase64(idCardRecord.getCardBitmap());
                CardRecord cardRecord = new CardRecord.Builder()
                        .cardType(idCardRecord.getCardType())
                        .cardId(idCardRecord.getCardId())
                        .name(idCardRecord.getName())
                        .birthday(idCardRecord.getBirthday())
                        .address(idCardRecord.getAddress())
                        .cardNumber(idCardRecord.getCardNumber())
                        .issuingAuthority(idCardRecord.getIssuingAuthority())
                        .validateStart(idCardRecord.getValidateStart())
                        .validateEnd(idCardRecord.getValidateEnd())
                        .sex(idCardRecord.getSex())
                        .nation(idCardRecord.getNation())
                        .passNumber(idCardRecord.getPassNumber())
                        .issueCount(idCardRecord.getIssueCount())
                        .chineseName(idCardRecord.getChineseName())
                        .version(idCardRecord.getVersion())
                        .cardPicture(cardPicture)
                        .facePicture(facePicture)
                        .result("比对成功")
                        .score(String.valueOf(score))
                        .verifyTime(ValueUtil.simpleDateFormat.format(new Date()))
                        .location(location)
                        .longitude(longitude)
                        .latitude(latitude)
                        .mode("1")
                        .deviceId(ConfigManager.getInstance().getConfig().getDeviceId())
                        .build();
                emitter.onNext(cardRecord);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<CardRecord, ObservableSource<ResponseEntity>>) cardRecord -> {
                        URL url = new URL(uploadUrl);
                        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                        UpLoadRecordNet upLoadRecordNet = retrofit.create(UpLoadRecordNet.class);
                        String json = new Gson().toJson(cardRecord);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecordNet.uploadData(uploadUrl, requestBody);
                    })
                    .subscribe(responseEntity -> {
                        Log.e("asd", "uploadCardRecord上传成功");
                    }, Throwable::printStackTrace);
        }
    }

    public void uploadWhiteCardRecord(IDCardRecord idCardRecord) {
        String uploadUrl = ConfigManager.getInstance().getConfig().getCardUploadUrl();
        if (!TextUtils.isEmpty(uploadUrl)) {
            Observable.create((ObservableOnSubscribe<CardRecord>) emitter -> {
                String latitude = "";
                String longitude = "";
                String location = "";
                if (AmapManager.getInstance().getaMapLocation() != null) {
                    latitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLatitude());
                    longitude = String.valueOf(AmapManager.getInstance().getaMapLocation().getLongitude());
                    location = AmapManager.getInstance().getaMapLocation().getAddress();
                }
                String cardPicture = FileUtil.bitmapToBase64(idCardRecord.getCardBitmap());
                CardRecord cardRecord = new CardRecord.Builder()
                        .cardType(idCardRecord.getCardType())
                        .cardId(idCardRecord.getCardId())
                        .name(idCardRecord.getName())
                        .birthday(idCardRecord.getBirthday())
                        .address(idCardRecord.getAddress())
                        .cardNumber(idCardRecord.getCardNumber())
                        .issuingAuthority(idCardRecord.getIssuingAuthority())
                        .validateStart(idCardRecord.getValidateStart())
                        .validateEnd(idCardRecord.getValidateEnd())
                        .sex(idCardRecord.getSex())
                        .nation(idCardRecord.getNation())
                        .passNumber(idCardRecord.getPassNumber())
                        .issueCount(idCardRecord.getIssueCount())
                        .chineseName(idCardRecord.getChineseName())
                        .version(idCardRecord.getVersion())
                        .verifyTime(ValueUtil.simpleDateFormat.format(new Date()))
                        .cardPicture(cardPicture)
                        .location(location)
                        .longitude(longitude)
                        .latitude(latitude)
                        .mode("2")
                        .deviceId(ConfigManager.getInstance().getConfig().getDeviceId())
                        .build();
                emitter.onNext(cardRecord);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<CardRecord, ObservableSource<ResponseEntity>>) cardRecord -> {
                        URL url = new URL(uploadUrl);
                        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                        UpLoadRecordNet upLoadRecordNet = retrofit.create(UpLoadRecordNet.class);
                        String json = new Gson().toJson(cardRecord);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecordNet.uploadData(uploadUrl, requestBody);
                    })
                    .subscribe(responseEntity -> {
                        Log.e("asd", "uploadWhiteCardRecord");
                    }, Throwable::printStackTrace);
        }
    }

    public void uploadAllNotUploadedRecord() throws MalformedURLException {
        String uploadUrl = ConfigManager.getInstance().getConfig().getUploadUrl();
        if (TextUtils.isEmpty(uploadUrl)) return;
        if (subscription != null) {
            subscription.cancel();
        }
        URL url = new URL(uploadUrl);
        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
        UpLoadRecordNet upLoadRecordNet = retrofit.create(UpLoadRecordNet.class);
        Flowable.create((FlowableOnSubscribe<Record>) emitter -> {
            while (!emitter.isCancelled()) {
                if (emitter.requested() == 0) {
                    Thread.sleep(500);
                    return;
                }
                Record record = RecordModel.loadNotUploadedRecord();
                if (record != null) {
                    emitter.onNext(record);
                } else {
                    break;
                }
            }
            if (!emitter.isCancelled()) {
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .doOnNext(record -> {
                    UploadRecord uploadRecord = new UploadRecord.Builder()
                            .cardNumber(record.getCardNumber())
                            .facePicture(FileUtil.pathToBase64(record.getFacePicture()))
                            .latitude(record.getLatitude())
                            .longitude(record.getLongitude())
                            .location(record.getLocation())
                            .sex(record.getSex())
                            .name(record.getName())
                            .verifyTime(record.getVerifyTime())
                            .score(record.getScore())
                            .mode("0")
                            .categoryId(record.getCategoryId())
                            .deviceId(ConfigManager.getInstance().getConfig().getDeviceId())
                            .build();
                    String json = new Gson().toJson(uploadRecord);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                    Call<ResponseEntity> responseEntityCall = upLoadRecordNet.uploadDataFlowable(uploadUrl, requestBody);
                    Response<ResponseEntity> response = responseEntityCall.execute();
                    ResponseEntity responseEntity = response.body();
                    if (responseEntity != null && TextUtils.equals(responseEntity.getCode(), "200")) {
                        record.setUpload(Boolean.TRUE);
                        RecordModel.updateRecord(record);
                    }
                })
                .subscribe(new FlowableSubscriber<Record>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(Record record) {
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        subscription.cancel();
                        t.printStackTrace();
                        Log.e("asd", "上传日志" + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        subscription.cancel();
                        clearRecordByThreshold();
                    }
                });
    }

    public void uploadPerson(Person person) {
        String uploadUrl = ConfigManager.getInstance().getConfig().getPersonUploadUrl();
        if (!TextUtils.isEmpty(uploadUrl)) {
            Observable.create((ObservableOnSubscribe<UploadPerson>) emitter -> {
                String facePicture = FileUtil.pathToBase64(person.getFacePicture());
                String cardPicture = FileUtil.pathToBase64(person.getCardPicture());
                UploadPerson uploadPerson = new UploadPerson.Builder()
                        .cardType(person.getCardType())
                        .cardId(person.getCardId())
                        .name(person.getName())
                        .birthday(person.getBirthday())
                        .address(person.getAddress())
                        .sex(person.getSex())
                        .cardNumber(person.getCardNumber())
                        .nation(person.getNation())
                        .validateStart(person.getValidateStart())
                        .validateEnd(person.getValidateEnd())
                        .issuingAuthority(person.getIssuingAuthority())
                        .passNumber(person.getPassNumber())
                        .issueCount(person.getIssueCount())
                        .chineseName(person.getChineseName())
                        .version(person.getVersion())
                        .faceFeature(person.getFaceFeature())
                        .facePicture(facePicture)
                        .cardPicture(cardPicture)
                        .score(person.getScore())
                        .registerTime(person.getRegisterTime())
                        .categoryId(person.getCategoryId())
                        .mode("3")
                        .deviceId(ConfigManager.getInstance().getConfig().getDeviceId())
                        .build();
                emitter.onNext(uploadPerson);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<UploadPerson, ObservableSource<ResponseEntity>>) uploadPerson -> {
                        URL url = new URL(uploadUrl);
                        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                        UpLoadRecordNet upLoadRecordNet = retrofit.create(UpLoadRecordNet.class);
                        String json = new Gson().toJson(uploadPerson);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecordNet.uploadData(uploadUrl, requestBody);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseEntity -> {
                        Log.e("asd", "uploadPerson上传成功");
                    }, Throwable::printStackTrace);
        }
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
                        UpLoadRecordNet upLoadRecordNet = retrofit.create(UpLoadRecordNet.class);
                        String json = new Gson().toJson(record);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecordNet.uploadData(uploadUrl, requestBody);
                    })
                    .subscribe(responseEntity -> {
                        if (TextUtils.equals(responseEntity.getCode(), "200")) {
                            record.setUpload(Boolean.TRUE);
                            RecordModel.updateRecord(record);
                        }
                    }, Throwable::printStackTrace);
        }
    }

    private void clearRecordByThreshold() {
        long recordCount = RecordModel.getRecordCount();
        if (recordCount > ConfigManager.getInstance().getConfig().getRecordClearThreshold()) {
            RecordModel.clearRecord((int) recordCount / 2);
        }
    }

}
