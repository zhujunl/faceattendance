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
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.model.net.UpLoadRecord;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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

    private Record recordCache;
    private ConcurrentLinkedQueue<Record> recordQueue = new ConcurrentLinkedQueue<>();

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
//                    uploadAllNotUploadedRecord(record);
                    demo18();
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
                        .build();
                emitter.onNext(cardRecord);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<CardRecord, ObservableSource<ResponseEntity>>) cardRecord -> {
                        URL url = new URL(uploadUrl);
                        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                        UpLoadRecord upLoadRecord = retrofit.create(UpLoadRecord.class);
                        String json = new Gson().toJson(cardRecord);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecord.uploadData(uploadUrl, requestBody);
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
                        .location(location)
                        .longitude(longitude)
                        .latitude(latitude)
                        .mode("2")
                        .build();
                emitter.onNext(cardRecord);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap((Function<CardRecord, ObservableSource<ResponseEntity>>) cardRecord -> {
                        URL url = new URL(uploadUrl);
                        Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                        UpLoadRecord upLoadRecord = retrofit.create(UpLoadRecord.class);
                        String json = new Gson().toJson(cardRecord);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                        return upLoadRecord.uploadData(uploadUrl, requestBody);
                    })
                    .subscribe(responseEntity -> {
                        Log.e("asd", "uploadWhiteCardRecord");
                    }, Throwable::printStackTrace);
        }
    }
    private volatile boolean flag = false;
    public void demo18() {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        int i = 0;
                        while (true) {
                            if (flag) continue;//此处添加代码，让flowable按需发送数据
                            System.out.println("发射---->" + i);
                            i++;
                            e.onNext(i);
                            flag = true;
                        }
                    }
                }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    private Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);            //设置初始请求数据量为1
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(50);
                            System.out.println("接收------>" + integer);
//                            mSubscription.request(1);//每接收到一条数据增加一条请求量
                            flag = false;
                        } catch (InterruptedException ignore) {
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void uploadAllNotUploadedRecord(Record mRecord) {
        if (!recordQueue.isEmpty()) {
            Log.e("asd", "recordQueue:add");
            recordQueue.offer(mRecord);
            return;
        } else {
            for (Record record : RecordModel.loadAllNotUploadedRecord()) {
                recordQueue.offer(record);
            }
        }
        Log.e("asd", recordQueue.size() + "");
        String uploadUrl = ConfigManager.getInstance().getConfig().getUploadUrl();
        if (TextUtils.isEmpty(uploadUrl)) return;
        Flowable.create((FlowableOnSubscribe<Record>) emitter -> {
            while (!recordQueue.isEmpty()) {
                if (emitter.requested() == 0) continue;
                Record poll = recordQueue.poll();
                Log.e("asd", poll.getId() + "开始下发");
                emitter.onNext(poll);
            }
            emitter.onComplete();
        }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<Record, Publisher<ResponseEntity>>) record -> {
                    Log.e("asd", record.getId() + "开始上传");
                    this.recordCache = record;
                    URL url = new URL(uploadUrl);
                    Retrofit retrofit = FaceAttendanceApp.RETROFIT.baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/").build();
                    record.setFacePicture(FileUtil.pathToBase64(record.getFacePicture()));
                    UpLoadRecord upLoadRecord = retrofit.create(UpLoadRecord.class);
                    String json = new Gson().toJson(record);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                    return upLoadRecord.uploadDataFlowable(uploadUrl, requestBody);
                }, 1)
                .doOnNext(responseEntity -> {
                    if (TextUtils.equals(responseEntity.getCode(), "200")) {
                        Log.e("asd", recordCache.getId() + "上传成功");
                        recordCache.setUpload(Boolean.TRUE);
                        RecordModel.updateRecord(recordCache);
                    }
                })
                .subscribe(new FlowableSubscriber<ResponseEntity>() {

                    private Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        mSubscription.request(1);
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        mSubscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        Log.e("asd", recordCache.getId() + "上传失败");
                    }

                    @Override
                    public void onComplete() {
                        clearRecordByThreshold();
                    }
                });
//        Observable.create((ObservableOnSubscribe<Record>) emitter -> {
//            List<Record> recordList = RecordModel.loadAllNotUploadedRecord();
//            for (Record record : recordList) {
//                emitter.onNext(record);
//            }
//            emitter.onComplete();
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .delay(1000, TimeUnit.MILLISECONDS)
//                .subscribe(this::uploadRecord, Throwable::printStackTrace, this::clearRecordByThreshold);
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
