package com.miaxis.faceattendance.server;

import com.miaxis.faceattendance.model.RecordModel;
import com.miaxis.faceattendance.model.net.ResponseEntity;

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

//    private ResponseEntity handleGetRecordList(NanoHTTPD.IHTTPSession session) {
//        Map<String, List<String>> parameters = session.getParameters();
//        if (parameters.get("pageNum") != null
//                && parameters.get("pageSize") != null
//                && parameters.get("name") != null
//                && parameters.get("cardNumber") != null
//                && parameters.get("result") != null
//                && parameters.get("date") != null
//                && parameters.get("upload") != null) {
//
//        }
//    }

    private ResponseEntity handleClearRecord(NanoHTTPD.IHTTPSession session) {
        RecordModel.clearAllRecord();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "删除日志成功");
    }

}
