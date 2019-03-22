package com.miaxis.faceattendance.event;

public class InitFaceEvent {

    public static final int ERR_LICENCE         = -2009;
    public static final int ERR_FILE_COMPARE    = -101;
    public static final int INIT_SUCCESS        = 0;

    private int result;

    public InitFaceEvent(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
