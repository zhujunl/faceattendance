package com.miaxis.faceattendance.server;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.service.HttpCommServerService;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class EditPersonServer {

    private static final String START_EDIT_PERSON = "/miaxis/attendance/editPersonServer/startEditPerson";
    private static final String ADD_PERSON_BY_PHOTO = "/miaxis/attendance/editPersonServer/addPersonByPhoto";
    private static final String ADD_PERSON_BY_FEATURE = "/miaxis/attendance/editPersonServer/addPersonByFeature";
    private static final String ADD_PERSON_LIST_BY_FEATURE = "/miaxis/attendance/editPersonServer/addPersonListByFeature";
    private static final String DELETE_PERSON_LIST = "/miaxis/attendance/editPersonServer/deletePersonList";
    private static final String CLEAR_PERSON = "/miaxis/attendance/editPersonServer/clearPerson";
    private static final String STOP_EDIT_PERSON = "/miaxis/attendance/editPersonServer/stopEditPerson";

    private HttpCommServerService.OnServerServiceListener listener;

    public EditPersonServer(HttpCommServerService.OnServerServiceListener listener) {
        this.listener = listener;
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case START_EDIT_PERSON: //开始编辑人员
                    return handleStartEditPerson(session);
                case ADD_PERSON_BY_PHOTO: //通过照片新增单个人员
                case ADD_PERSON_BY_FEATURE: //通过人脸特征新增单个人员
                case ADD_PERSON_LIST_BY_FEATURE: //通过特征批量添加人员
                case DELETE_PERSON_LIST: //批量删除人员
                    return handleDeletePersonList(session);
                case CLEAR_PERSON: //清除所有人员
                    return handleClearPerson(session);
                case STOP_EDIT_PERSON: //结束编辑人员
                    return handleStopEditPerson(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

    private ResponseEntity handleStartEditPerson(NanoHTTPD.IHTTPSession session) {
        listener.onStartEditPerson();
        return new ResponseEntity(AttendanceServer.SUCCESS, "开始编辑人员成功，请查看设备是否进入人员管理页面");
    }

    private ResponseEntity handleDeletePersonList(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("cardNumberList") != null) {
            String json = parameters.get("cardNumberList").get(0);
            if (!TextUtils.isEmpty(json)) {
                List<String> cardNumberList = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
                listener.onDeletePerson(true);
                for (String cardNumber : cardNumberList) {
                    PersonModel.deletePersonByCardNumber(cardNumber);
                }
                listener.onDeletePerson(false);
                return new ResponseEntity(AttendanceServer.SUCCESS, "删除人员成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleClearPerson(NanoHTTPD.IHTTPSession session) {
        if (listener.isPersonFragmentVisible()) {
            listener.onClearPerson(true);
            PersonModel.clearPerson();
            listener.onClearPerson(false);
            return new ResponseEntity(AttendanceServer.SUCCESS, "清除人员成功");
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

    private ResponseEntity handleStopEditPerson(NanoHTTPD.IHTTPSession session) {
        listener.onStopEditPerson();
        return new ResponseEntity(AttendanceServer.SUCCESS, "结束编辑人员成功，请查看设备是否返回人脸考勤页面");
    }

}
