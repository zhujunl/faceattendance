package com.miaxis.faceattendance.model.net;

/**
 * Created by tang.yf on 2018/2/23.
 */

public class ResponseEntity<E> {
    private String code;
    private String message;
    private E data;

    public ResponseEntity() {
    }

    public ResponseEntity(String code, String message, E data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
