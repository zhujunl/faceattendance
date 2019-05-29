package com.miaxis.faceattendance.server;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.miaxis.faceattendance.manager.ConfigManager;
import com.miaxis.faceattendance.model.net.ResponseEntity;
import com.miaxis.faceattendance.service.HttpCommServerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.miaxis.faceattendance.model.net.NanoHTTPD;

public class AttendanceServer extends NanoHTTPD {

    public static final String SUCCESS = "200";
    public static final String FAILED = "400";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";

    private BasisServer basisServer;
    private PersonServer personServer;
    private RecordServer recordServer;
    private EditPersonServer editPersonServer;
    private WhitelistServer whitelistServer;

    public AttendanceServer(int port, HttpCommServerService.OnServerServiceListener listener) {
        super(port);
        basisServer = new BasisServer(listener);
        personServer = new PersonServer();
        recordServer = new RecordServer();
        editPersonServer = new EditPersonServer(listener);
        whitelistServer = new WhitelistServer();
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (Method.POST.equals(session.getMethod()) && session.getUri().startsWith("/miaxis/attendance/")) {
            if (checkPassword(session)) {
                ResponseEntity responseEntity = null;
                if (session.getUri().startsWith("/miaxis/attendance/baseServer/")) {
                    responseEntity = basisServer.handleRequest(session);
                }
                if (session.getUri().startsWith("/miaxis/attendance/personServer/")) {
                    responseEntity = personServer.handleRequest(session);
                }
                if (session.getUri().startsWith("/miaxis/attendance/recordServer/")) {
                    responseEntity = recordServer.handleRequest(session);
                }
                if (session.getUri().startsWith("/miaxis/attendance/editPersonServer/")) {
                    responseEntity = editPersonServer.handleRequest(session);
                }
                if (session.getUri().startsWith("/miaxis/attendance/whitelistServer/")) {
                    responseEntity = whitelistServer.handleRequest(session);
                }
                return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(responseEntity));
            }
            return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(new ResponseEntity(FORBIDDEN, "Forbidden")));
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, new Gson().toJson(new ResponseEntity(NOT_FOUND, "Not Found")));
    }

    private boolean checkPassword(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> parameters = session.getParameters();
        if (parameters != null && parameters.get("password") != null) {
            String password = parameters.get("password").get(0);
            return TextUtils.equals(ConfigManager.getInstance().getConfig().getPassword(), password);
        }
        return false;
    }

}