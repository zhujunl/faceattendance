package com.miaxis.faceattendance.event;

import com.miaxis.faceattendance.model.entity.IDCardRecord;

import androidx.annotation.NonNull;

public class CardEvent {

    public final static int FIND_CARD = 1;
    public final static int READ_CARD = 2;
    public final static int NO_CARD = 3;
    public final static int OVERDUE = 4;

    private int mode;
    private IDCardRecord idCardRecord;

    public CardEvent(int mode) {
        this.mode = mode;
    }

    public CardEvent(@NonNull IDCardRecord idCardRecord) {
        this.mode = READ_CARD;
        this.idCardRecord = idCardRecord;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public IDCardRecord getIdCardRecord() {
        return idCardRecord;
    }

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }
}
