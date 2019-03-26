package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.local.greenDao.gen.RecordDao;

import java.util.List;

public class RecordModel {

    public static void saveRecord(Record record) {
        DaoManager.getInstance().getDaoSession().getRecordDao().insert(record);
    }

    public static List<Record> loadRecordList(int pageNum, int pageSize) {
        return DaoManager.getInstance().getDaoSession().getRecordDao().queryBuilder()
                .orderDesc(RecordDao.Properties.VerifyTime)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

}
