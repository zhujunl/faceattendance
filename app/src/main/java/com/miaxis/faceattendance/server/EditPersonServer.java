package com.miaxis.faceattendance.server;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.faceattendance.manager.FaceManager;
import com.miaxis.faceattendance.model.PersonModel;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.service.HttpCommServerService;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.fragment.PersonFragment;
import com.miaxis.faceattendance.view.fragment.VerifyFragment;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class EditPersonServer {

    private static final String START_EDIT_PERSON = "/miaxis/attendance/editPersonServer/startEditPerson";
    private static final String ADD_PERSON_BY_PHOTO = "/miaxis/attendance/editPersonServer/addPersonByPhoto";
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
                    return handleAddPersonByPhoto(session);
                case ADD_PERSON_LIST_BY_FEATURE: //通过特征批量添加人员
                    return handleAddPersonListByFeature(session);
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
        boolean result = listener.onEnterFragment(PersonFragment.class, null);
        if (result) {
            return new ResponseEntity(AttendanceServer.SUCCESS, "开始编辑人员成功，请查看设备是否进入人员管理页面");
        } else {
            return new ResponseEntity(AttendanceServer.FAILED, "请确保应用界面可见");
        }
    }

    private ResponseEntity handleAddPersonByPhoto(NanoHTTPD.IHTTPSession session) throws IOException {
        if (listener.isPersonFragmentVisible()) {
            Map<String, List<String>> parameters = session.getParameters();
            if (parameters.get("personData") != null) {
                String json = parameters.get("personData").get(0).trim();
                Person person = new Gson().fromJson(json, Person.class);
                if (!TextUtils.isEmpty(person.getName())
                        && !TextUtils.isEmpty(person.getCardNumber())
                        && !TextUtils.isEmpty(person.getSex())
                        && !TextUtils.isEmpty(person.getFacePicture())) {
                    listener.onBackstageBusy(true, "正在提取特征");
                    byte[] imageData = Base64.decode(person.getFacePicture(), Base64.NO_WRAP);
                    StringBuilder stringBuilder = new StringBuilder();
                    byte[] feature = FaceManager.getInstance().getFeatureByFileImage(imageData, stringBuilder);
                    if (feature != null) {
                        person.setCardNumber(person.getCardNumber().replaceAll("\\p{P}", ""));
                        person.setFaceFeature(Base64.encodeToString(feature, Base64.NO_WRAP));
                        String path = FileUtil.FACE_IMG_PATH + File.separator + person.getCardNumber() + System.currentTimeMillis() + ".jpg";
                        FileUtil.createFileWithByte(imageData, path);
                        person.setFacePicture(path);
                        person.setId(null);
                        if (new File(path).exists()) {
                            PersonModel.savePerson(person);
                            listener.onBackstageBusy(false, "添加人员成功");
                            return new ResponseEntity(AttendanceServer.SUCCESS, "添加人员成功");
                        } else {
                            listener.onBackstageBusy(false, "添加人员失败");
                            return new ResponseEntity(AttendanceServer.SUCCESS, "人像图片落地失败");
                        }
                    } else {
                        listener.onBackstageBusy(false, "添加人员失败");
                        return new ResponseEntity(AttendanceServer.FAILED, stringBuilder.toString());
                    }
                }
            }
            return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

    private ResponseEntity handleAddPersonListByFeature(NanoHTTPD.IHTTPSession session) throws IOException {
        if (listener.isPersonFragmentVisible()) {
            Map<String, List<String>> parameters = session.getParameters();
            if (parameters.get("personDataList") != null) {
                String json = parameters.get("personDataList").get(0).trim();
                List<Person> personList = new Gson().fromJson(json, new TypeToken<List<Person>>() {}.getType());
                boolean check = true;
                for (Person person : personList) {
                    if (TextUtils.isEmpty(person.getName())
                            || TextUtils.isEmpty(person.getCardNumber())
                            || TextUtils.isEmpty(person.getSex())
                            || TextUtils.isEmpty(person.getFaceFeature())
                            || TextUtils.isEmpty(person.getFacePicture())) {
                        check = false;
                        break;
                    } else {
                        person.setId(null);
                        person.setRegisterTime(ValueUtil.simpleDateFormat.format(new Date()));
                    }
                }
                if (check) {
                    StringBuilder error = new StringBuilder();
                    listener.onBackstageBusy(true, "批量添加人员处理中");
                    for (Person person : personList) {
                        byte[] imageData = Base64.decode(person.getFacePicture(), Base64.NO_WRAP);
                        person.setCardNumber(person.getCardNumber().replaceAll("\\p{P}", ""));
                        String path = FileUtil.FACE_IMG_PATH + File.separator + person.getCardNumber() + System.currentTimeMillis() + ".jpg";
                        FileUtil.createFileWithByte(imageData, path);
                        person.setFacePicture(path);
                        if (new File(path).exists()) {
                            PersonModel.savePerson(person);
                        } else {
                            error.append(person.getName()).append("-").append(person.getCardNumber()).append(";");
                        }
                    }
                    listener.onBackstageBusy(false, "批量添加人员成功");
                    return new ResponseEntity(AttendanceServer.SUCCESS, "批量添加人员成功" + (error.toString().isEmpty() ? "" : "，但是：" + error.toString() + "人员绑定失败"));
                }
            }
            return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

    private ResponseEntity handleDeletePersonList(NanoHTTPD.IHTTPSession session) {
        if (listener.isPersonFragmentVisible()) {
            Map<String, List<String>> parameters = session.getParameters();
            if (parameters.get("cardNumberList") != null) {
                String json = parameters.get("cardNumberList").get(0);
                if (!TextUtils.isEmpty(json)) {
                    List<String> cardNumberList = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
                    listener.onBackstageBusy(true, "正在删除人员");
                    for (String cardNumber : cardNumberList) {
                        PersonModel.deletePersonByCardNumber(cardNumber);
                    }
                    listener.onBackstageBusy(false, "删除人员成功");
                    return new ResponseEntity(AttendanceServer.SUCCESS, "删除人员成功");
                }
            }
            return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

    private ResponseEntity handleClearPerson(NanoHTTPD.IHTTPSession session) {
        if (listener.isPersonFragmentVisible()) {
            listener.onBackstageBusy(true, "正在清除人员");
            PersonModel.clearPerson();
            listener.onBackstageBusy(false, "清除人员成功");
            return new ResponseEntity(AttendanceServer.SUCCESS, "清除人员成功");
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

    private ResponseEntity handleStopEditPerson(NanoHTTPD.IHTTPSession session) {
        if (listener.isPersonFragmentVisible()) {
            boolean result = listener.onEnterFragment(VerifyFragment.class, null);
            if (result) {
                return new ResponseEntity(AttendanceServer.SUCCESS, "结束编辑人员成功，请查看设备是否返回人脸考勤页面");
            } else {
                return new ResponseEntity(AttendanceServer.FAILED, "请确保应用界面可见");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "请在人员管理页面进行操作");
    }

}
