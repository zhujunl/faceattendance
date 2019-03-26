package com.miaxis.faceattendance.server;

import com.miaxis.faceattendance.model.net.ResponseEntity;

import fi.iki.elonen.NanoHTTPD;

public class PersonServer {

    private static final String FIND_PERSON_BY_CARD_NUMBER = "/miaxis/attendance/personServer/findPersonByCardNumber";
    private static final String GET_PERSON_COUNT = "/miaxis/attendance/personServer/getPersonCount";
    private static final String GET_PERSON_LIST = "/miaxis/attendance/personServer/getPersonList";
    private static final String START_EDIT_PERSON = "/miaxis/attendance/personServer/startEditPerson";
    private static final String ADD_PERSON_BY_PHOTO = "/miaxis/attendance/personServer/addPersonByPhoto";
    private static final String ADD_PERSON_BY_FEATURE = "/miaxis/attendance/personServer/addPersonByFeature";
    private static final String ADD_PERSON_LIST_BY_FEATURE = "/miaxis/attendance/personServer/addPersonListByFeature";
    private static final String DELETE_PERSON_LIST = "/miaxis/attendance/personServer/deletePersonList";
    private static final String CLEAR_PERSON = "/miaxis/attendance/personServer/clearPerson";
    private static final String STOP_EDIT_PERSON = "/miaxis/attendance/personServer/stopEditPerson";

    public PersonServer() {
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case FIND_PERSON_BY_CARD_NUMBER: //通过证件号码查询人员
                case GET_PERSON_COUNT: //获得库中人员总数
                case GET_PERSON_LIST: //分页获取人员
                case START_EDIT_PERSON: //开始编辑人员
                case ADD_PERSON_BY_PHOTO: //通过照片新增单个人员
                case ADD_PERSON_BY_FEATURE: //通过人脸特征新增单个人员
                case ADD_PERSON_LIST_BY_FEATURE: //通过特征批量添加人员
                case DELETE_PERSON_LIST: //批量删除人员
                case CLEAR_PERSON: //清除所有人员
                case STOP_EDIT_PERSON: //结束编辑人员
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

}
