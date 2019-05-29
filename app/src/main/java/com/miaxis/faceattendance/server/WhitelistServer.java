package com.miaxis.faceattendance.server;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miaxis.faceattendance.model.WhiteCardModel;
import com.miaxis.faceattendance.model.entity.WhiteCard;
import com.miaxis.faceattendance.model.net.NanoHTTPD;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.util.ValueUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WhitelistServer {

    private static final String ADD_WHITELIST = "/miaxis/attendance/whitelistServer/addWhitelist";
    private static final String GET_WHITELIST_COUNT = "/miaxis/attendance/whitelistServer/getWhitelistCount";
    private static final String GET_WHITELIST = "/miaxis/attendance/whitelistServer/getWhitelist";
    private static final String DELETE_WHITELIST = "/miaxis/attendance/whitelistServer/deleteWhitelist";
    private static final String CLEAR_WHITELIST = "/miaxis/attendance/whitelistServer/clearWhitelist";

    public WhitelistServer() {
    }

    public ResponseEntity handleRequest(NanoHTTPD.IHTTPSession session) {
        try {
            switch (session.getUri()) {
                case ADD_WHITELIST: //新增白名单
                    return handleAddWhitelist(session);
                case GET_WHITELIST_COUNT: //获取白名单总数
                    return handleGetWhitelistCount(session);
                case GET_WHITELIST: //分页获取白名单
                    return handleGetWhitelist(session);
                case DELETE_WHITELIST: //删除白名单
                    return handleDeleteWhitelist(session);
                case CLEAR_WHITELIST: //清除白名单
                    return handleClearWhitelist(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(AttendanceServer.FAILED, "服务器错误");
        }
        return new ResponseEntity(AttendanceServer.NOT_FOUND, "Not Found");
    }

    private ResponseEntity handleAddWhitelist(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("whitelist") != null) {
            String json = parameters.get("whitelist").get(0).trim();
            List<WhiteCard> whiteCardList = new Gson().fromJson(json, new TypeToken<List<WhiteCard>>() {}.getType());
            boolean check = true;
            for (WhiteCard whiteCard : whiteCardList) {
                if (TextUtils.isEmpty(whiteCard.getName())
                        || TextUtils.isEmpty(whiteCard.getCardNumber())
                        || !ValueUtil.isIDNumber(whiteCard.getCardNumber())) {
                    check = false;
                    break;
                } else {
                    whiteCard.setRegisterTime(ValueUtil.simpleDateFormat.format(new Date()));
                }
            }
            if (check) {
                WhiteCardModel.saveWhiteCardList(whiteCardList);
                return new ResponseEntity(AttendanceServer.SUCCESS, "批量添加白名单成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleGetWhitelistCount(NanoHTTPD.IHTTPSession session) {
        long whitelistCount = WhiteCardModel.getWhiteCardCount();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "获取白名单数目成功", whitelistCount);
    }

    private ResponseEntity handleGetWhitelist(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("pageNum") != null && parameters.get("pageSize") != null) {
            int pageNum = Integer.valueOf(parameters.get("pageNum").get(0));
            int pageSize = Integer.valueOf(parameters.get("pageSize").get(0));
            if (pageNum > 0 && pageSize > 0 && pageSize <= 10) {
                List<WhiteCard> whiteCardList = WhiteCardModel.loadWhitelist(pageNum, pageSize);
                return new ResponseEntity<>(AttendanceServer.SUCCESS, "分页获取白名单成功", whiteCardList);
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleDeleteWhitelist(NanoHTTPD.IHTTPSession session) {
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters.get("cardNumberList") != null) {
            String json = parameters.get("cardNumberList").get(0);
            if (!TextUtils.isEmpty(json)) {
                List<String> cardNumberList = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
                WhiteCardModel.deleteWhileCardList(cardNumberList);
                return new ResponseEntity(AttendanceServer.SUCCESS, "批量删除白名单成功");
            }
        }
        return new ResponseEntity(AttendanceServer.FAILED, "参数校验失败");
    }

    private ResponseEntity handleClearWhitelist(NanoHTTPD.IHTTPSession session) {
        WhiteCardModel.clearWhitelist();
        return new ResponseEntity<>(AttendanceServer.SUCCESS, "清除白名单成功");
    }

}
