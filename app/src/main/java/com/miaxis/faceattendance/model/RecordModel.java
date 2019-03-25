package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Record;

public class RecordModel {

    public static void saveRecord(Record record) {
        DaoManager.getInstance().getDaoSession().getRecordDao().insert(record);
    }

}
