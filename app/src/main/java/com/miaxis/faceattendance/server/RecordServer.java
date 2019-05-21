package com.miaxis.faceattendance.server;

import android.text.TextUtils;
import android.util.Base64;

import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.entity.UploadRecord;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class RecordServer {

    private static final String GET_RECORD_COUNT = "/miaxis/attendance/recordServer/getRecordCount";
    private static final String GET_RECORD_LIST = "/miaxis/attendance/recordServer/getRecordList";
    private static final String CLEAT_RECORD = "/miaxis/attendance/recordServer/clearRecord";

    public RecordServer() {
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case GET_RECORD_COUNT: //获取日志总数
                    return handleGetRecordCount(session);
                case GET_RECORD_LIST: //分页获取日志信息
                    return handleGetRecordList(session);
                case CLEAT_RECORD: //清除所有日志
                    return handleClearRecord(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

    private ResponseEntity handleGetRecordCount(NanoHTTPD.IHTTPSession session) {
        long recordCount = RecordModel.getRecordCount();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取日志数目成功", recordCount);
    }

    private ResponseEntity handleGetRecordList(NanoHTTPD.IHTTPSession session) throws ParseException {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("pageNum") != null && parameters.get("pageSize") != null) {
            int pageNum = Integer.valueOf(parameters.get("pageNum").get(0));
            int pageSize = Integer.valueOf(parameters.get("pageSize").get(0));
            if (pageNum > 0 && pageSize > 0 && pageSize <= 10) {
                String name = parameters.get("name") != null ? parameters.get("name").get(0) : "";
                String sex = parameters.get("sex") != null ? parameters.get("sex").get(0) : "";
                String cardNumber = parameters.get("cardNumber") != null ? parameters.get("cardNumber").get(0) : "";
                String startDate = parameters.get("startDate") != null ? parameters.get("startDate").get(0) : "";
                String endDate = parameters.get("endDate") != null ? parameters.get("endDate").get(0) : "";
                Boolean upload = parameters.get("upload") != null ? Boolean.valueOf(parameters.get("upload").get(0)) : null;
                if ((TextUtils.isEmpty(startDate) && TextUtils.isEmpty(endDate))
                        || (ValueUtil.isValidDate(startDate) && ValueUtil.isValidDate(endDate))) {
                    List<Record> recordList = RecordModel.queryRecord(pageNum, pageSize, name, sex, cardNumber, startDate, endDate, upload);
                    List<UploadRecord> uploadRecordList = new ArrayList<>();
                    for (Record record : recordList) {
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
                        uploadRecordList.add(uploadRecord);
                    }
                    return new ResponseEntity<>(AttendanceServer.SUCCESS, "查询日志成功", uploadRecordList);
                }
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleClearRecord(NanoHTTPD.IHTTPSession session) {
        RecordModel.clearAllRecord();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "清除日志成功");
    }

}
