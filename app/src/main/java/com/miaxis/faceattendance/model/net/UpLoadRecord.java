package com.miaxis.faceattendance.model.net;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UpLoadRecord {

    @Headers("Content-Type:application/json;charset=utf-8")
    @POST
    Observable<ResponseEntity> uploadData(@Url String url, @Body RequestBody requestBody);

}
