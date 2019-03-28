package com.miaxis.faceattendance.server;

import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.local.greenDao.gen.PersonDao;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.util.FileUtil;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class PersonServer {

    private static final String FIND_PERSON_BY_CARD_NUMBER = "/miaxis/attendance/personServer/findPersonByCardNumber";
    private static final String GET_PERSON_COUNT = "/miaxis/attendance/personServer/getPersonCount";
    private static final String GET_PERSON_LIST = "/miaxis/attendance/personServer/getPersonList";

    public PersonServer() {
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case FIND_PERSON_BY_CARD_NUMBER: //通过证件号码查询人员
                    return handleFindPersonByCardNumber(session);
                case GET_PERSON_COUNT: //获得库中人员总数
                    return handleGetPersonCount(session);
                case GET_PERSON_LIST: //分页获取人员
                    return handleGetPersonList(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

    private ResponseEntity handleFindPersonByCardNumber(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("cardNumber") != null) {
            String cardNumber = parameters.get("cardNumber").get(0);
            Person person = PersonModel.getPersonByCardNumber(cardNumber);
            if (person == null) {
                return new ResponseEntity(AttendanceServer.SUCCESS, "未找到该人员");
            } else {
                person.setFacePicture(FileUtil.pathToBase64(person.getFacePicture()));
                return new ResponseEntity<>(AttendanceServer.SUCCESS, "查询人员成功", person);
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleGetPersonCount(NanoHTTPD.IHTTPSession session) {
        long personCount = PersonModel.getPersonCount();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取人员数目成功", personCount);
    }

    private ResponseEntity handleGetPersonList(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("pageNum") != null && parameters.get("pageSize") != null) {
            int pageNum = Integer.valueOf(parameters.get("pageNum").get(0));
            int pageSize = Integer.valueOf(parameters.get("pageSize").get(0));
            if (pageNum > 0 && pageSize > 0) {
                List<Person> personList = PersonModel.loadPersonList(pageNum, pageSize);
                for (Person person : personList) {
                    person.setFacePicture(FileUtil.pathToBase64(person.getFacePicture()));
                }
                return new ResponseEntity<>(AttendanceServer.SUCCESS, "分页获取人员成功", personList);
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

}
