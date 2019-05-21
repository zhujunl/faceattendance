package com.miaxis.faceattendance.server;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.faceattendance.app.FaceAttendanceApp;
import com.miaxis.faceattendance.manager.CategoryManager;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.model.CategoryModel;
import com.miaxis.faceattendance.model.ConfigModel;
import com.miaxis.faceattendance.model.entity.Category;
import com.miaxis.faceattendance.model.entity.Config;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.service.HttpCommServerService;
import com.miaxis.faceattendance.util.ValueUtil;
import com.miaxis.faceattendance.view.fragment.UpdateFragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class BasisServer {

    private static final String CONNECTIVITY_DETECTION = "/miaxis/attendance/baseServer/connectivityDetection";
    private static final String SET_DEVICE_PASSWORD = "/miaxis/attendance/baseServer/setDevicePassword";
    private static final String GET_SYSTEM_TIME = "/miaxis/attendance/baseServer/getSystemTime";
    private static final String SET_SYSTEM_TIME = "/miaxis/attendance/baseServer/setSystemTime";
    private static final String SET_RECORD_UPLOAD_URL = "/miaxis/attendance/baseServer/setRecordUploadUrl";
    private static final String SET_VERIFY_THRESHOLD = "/miaxis/attendance/baseServer/setVerifyThreshold";
    private static final String SET_RECORD_CLEAR_THRESHOLD = "/miaxis/attendance/baseServer/setRecordClearThreshold";
    private static final String SET_VOICE_PROMPT = "/miaxis/attendance/baseServer/setVoicePrompt";
    private static final String GET_VERSION_NUMBER = "/miaxis/attendance/baseServer/getVersionNumber";
    private static final String VERSION_UPDATE = "/miaxis/attendance/baseServer/versionUpdate";
    private static final String SET_DEVICE_ID = "/miaxis/attendance/baseServer/setDeviceId";
    private static final String GET_DEVICE_ID = "/miaxis/attendance/baseServer/getDeviceId";
    private static final String SET_CATEGORY = "/miaxis/attendance/baseServer/setCategory";

    private HttpCommServerService.OnServerServiceListener listener;

    public BasisServer(HttpCommServerService.OnServerServiceListener listener) {
        this.listener = listener;
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case CONNECTIVITY_DETECTION: //检测连接性
                    return new ResponseEntity("200", "设备在线");
                case SET_DEVICE_PASSWORD: //设置设备密码
                    return handleSetDevicePassword(session);
                case GET_SYSTEM_TIME: //获取系统时间
                    return handleGetSystemTime(session);
                case SET_SYSTEM_TIME: //设置系统时间
                    return handleSetSystemTime(session);
                case SET_RECORD_UPLOAD_URL: //设置日志上传路径
                    return handleSetRecordUploadUrl(session);
                case SET_VERIFY_THRESHOLD: //设置比对阈值
                    return handleSetVerifyThreshold(session);
                case SET_RECORD_CLEAR_THRESHOLD:
                    return handleSetRecordClearThreshold(session);
                case SET_VOICE_PROMPT: // 设置语音提示
                    return handleSetVoicePrompt(session);
                case GET_VERSION_NUMBER: //获取版本号
                    return handleGetVersionNumber(session);
                case VERSION_UPDATE: //版本更新
                    return handleVersionUpdate(session);
                case SET_DEVICE_ID: //设置设备标识
                    return handleSetDeviceId(session);
                case GET_DEVICE_ID: //获取设备标识
                    return handleGetDeviceId(session);
                case SET_CATEGORY: //设置人员类别
                    return handleSetCategory(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

    private ResponseEntity handleSetDevicePassword(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("newPassword") != null && parameters.get("checkPassword") != null) {
            String newPassword = parameters.get("newPassword").get(0);
            String checkPassword = parameters.get("checkPassword").get(0);
            Config config = ConfigManager.getInstance().getConfig();
            if (newPassword.length() == 6
                    && !TextUtils.equals(config.getPassword(), newPassword)
                    && TextUtils.equals(newPassword, checkPassword)) {
                config.setPassword(newPassword);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置设备密码成功");
            } else {
                return new ResponseEntity(AttendanceServer.FAILED, "设置设备密码失败");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleGetSystemTime(NanoHTTPD.IHTTPSession session) {
        String time = ValueUtil.simpleDateFormat.format(new Date());
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取系统时间成功", time);
    }

    private synchronized ResponseEntity handleSetSystemTime(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("systemTime") != null) {
            String systemTime = parameters.get("systemTime").get(0);
            Date date = null;
            try {
                date = ValueUtil.simpleDateFormat.parse(systemTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                long when = calendar.getTimeInMillis();
                ((AlarmManager) FaceAttendanceApp.getInstance().getSystemService(Context.ALARM_SERVICE)).setTime(when);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置系统时间成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleSetRecordUploadUrl(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("uploadUrl") != null
                && parameters.get("cardUploadUrl") != null
                && parameters.get("personUploadUrl") != null) {
            String uploadUrl = parameters.get("uploadUrl").get(0);
            String cardUploadUrl = parameters.get("cardUploadUrl").get(0);
            String personUploadUrl = parameters.get("personUploadUrl").get(0);
            Config config = ConfigManager.getInstance().getConfig();
            if (TextUtils.isEmpty(uploadUrl)
                    || ValueUtil.isHttpFormat(cardUploadUrl)
                    || ValueUtil.isHttpFormat(personUploadUrl)) {
                config.setUploadUrl(uploadUrl);
                config.setCardUploadUrl(cardUploadUrl);
                config.setPersonUploadUrl(personUploadUrl);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置数据上传地址成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleSetVerifyThreshold(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("verifyScore") != null
                && parameters.get("cardVerifyScore") != null
                && parameters.get("verifyQualityScore") != null
                && parameters.get("registerQualityScore") != null) {
            String verifyScore = parameters.get("verifyScore").get(0);
            String cardVerifyScore = parameters.get("cardVerifyScore").get(0);
            String verifyQualityScore = parameters.get("verifyQualityScore").get(0);
            String registerQualityScore = parameters.get("registerQualityScore").get(0);
            float verify = 0;
            float cardVerify = 0;
            int verifyQuality = 0;
            int registerQuality = 0;
            try {
                verify = Float.valueOf(verifyScore);
                cardVerify = Float.valueOf(cardVerifyScore);
                verifyQuality = Integer.valueOf(verifyQualityScore);
                registerQuality = Integer.valueOf(registerQualityScore);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (verify >= 0.6f && verify < 1.0
                    && cardVerify >= 0.6f && cardVerify < 1.0
                    && verifyQuality >= 60 && verifyQuality < 100
                    && registerQuality >= 80 && registerQuality < 100) {
                Config config = ConfigManager.getInstance().getConfig();
                config.setVerifyQualityScore(verifyQuality);
                config.setRegisterQualityScore(registerQuality);
                config.setVerifyScore(verify);
                config.setCardVerifyScore(cardVerify);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置比对阈值成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleSetRecordClearThreshold(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("recordClearThreshold") != null) {
            String recordClearThreshold = parameters.get("recordClearThreshold").get(0);
            int threshold = 0;
            try {
                threshold = Integer.valueOf(recordClearThreshold);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (threshold >= 100 && threshold <= 1000) {
                Config config = ConfigManager.getInstance().getConfig();
                config.setRecordClearThreshold(threshold);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置日志清除阈值成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleSetVoicePrompt(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("attendancePrompt") != null
                || parameters.get("cardVerifyPrompt") != null
                || parameters.get("whitelistPrompt") != null) {
            String attendancePrompt = parameters.get("attendancePrompt").get(0);
            String cardVerifyPrompt = parameters.get("cardVerifyPrompt").get(0);
            String whitelistPrompt = parameters.get("whitelistPrompt").get(0);
            if (!TextUtils.isEmpty(attendancePrompt)
                    || !TextUtils.isEmpty(cardVerifyPrompt)
                    || !TextUtils.isEmpty(whitelistPrompt)) {
                Config config = ConfigManager.getInstance().getConfig();
                config.setAttendancePrompt(attendancePrompt);
                config.setCardVerifyPrompt(cardVerifyPrompt);
                config.setWhitelistPrompt(whitelistPrompt);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置语音提示成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleGetVersionNumber(NanoHTTPD.IHTTPSession session) {
        String version = ValueUtil.getCurVersion(FaceAttendanceApp.getInstance());
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取版本号成功", version);
    }

    private ResponseEntity handleVersionUpdate(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("fileUrl") != null) {
            String fileUrl = parameters.get("fileUrl").get(0);
            URL url = null;
            try {
                url = new URL(fileUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("url", url);
                boolean result = listener.onEnterFragment(UpdateFragment.class, bundle);
                if (result) {
                    return new ResponseEntity(AttendanceServer.SUCCESS, "调用更新请求成功，请查看是否开始下载");
                } else {
                    return new ResponseEntity(AttendanceServer.FAILED, "请确保应用界面可见");
                }
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleSetDeviceId(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("deviceId") != null) {
            String deviceId = parameters.get("deviceId").get(0);
            if (!TextUtils.isEmpty(deviceId)) {
                Config config = ConfigManager.getInstance().getConfig();
                config.setDeviceId(deviceId);
                ConfigModel.saveConfig(config);
                ConfigManager.getInstance().setConfig(config);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置设备标识成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

    private ResponseEntity handleGetDeviceId(NanoHTTPD.IHTTPSession session) {
        String deviceId = ConfigManager.getInstance().getConfig().getDeviceId();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取设备标识成功", deviceId);
    }

    private ResponseEntity handleSetCategory(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("category") != null) {
            String json = parameters.get("category").get(0);
            List<Category> categoryList = new Gson().fromJson(json, new TypeToken<List<Category>>() {}.getType());
            boolean check = true;
            for (Category category : categoryList) {
                if (category.getId() == 0
                        || TextUtils.isEmpty(category.getCategoryName())
                        || TextUtils.isEmpty(category.getCategoryPrompt())) {
                    check = false;
                    break;
                }
            }
            if (check) {
                CategoryModel.clearCategory();
                CategoryModel.saveCategoryList(categoryList);
                CategoryManager.getInstance().setCategoryList(categoryList);
                return new ResponseEntity(AttendanceServer.SUCCESS, "设置人员类别成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验错误");
    }

}
