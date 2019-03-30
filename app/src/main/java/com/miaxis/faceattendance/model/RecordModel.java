package com.miaxis.faceattendance.model;

import android.text.TextUtils;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.local.greenDao.gen.RecordDao;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.text.ParseException;
import java.util.List;

public class RecordModel {

    public static void saveRecord(Record record) {
        DaoManager.getInstance().getDaoSession().getRecordDao().insert(record);
    }

    public static void updateRecord(Record record) {
        DaoManager.getInstance().getDaoSession().getRecordDao().update(record);
    }

    public static List<Record> loadRecordList(int pageNum, int pageSize) {
        return DaoManager.getInstance().getDaoSession().getRecordDao().queryBuilder()
                .orderDesc(RecordDao.Properties.VerifyTime)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

    public static long getRecordCount() {
        return DaoManager.getInstance().getDaoSession().getRecordDao().count();
    }

    public static void clearAllRecord() {
        DaoManager.getInstance().getDaoSession().getRecordDao().deleteAll();
        FileUtil.deleteDirectoryFile(new File(FileUtil.IMG_PATH));
    }

    public static List<Record> queryRecord(int pageNum, int pageSize, String name, String sex, String cardNumber, String startDate, String endDate, Boolean upload) throws ParseException {
        QueryBuilder<Record> queryBuilder = DaoManager.getInstance().getDaoSession().getRecordDao().queryBuilder();
        if (!TextUtils.isEmpty(name)) {
            queryBuilder.where(RecordDao.Properties.Name.eq(name));
        }
        if (!TextUtils.isEmpty(sex)) {
            if (TextUtils.equals(sex, "男")) {
                queryBuilder.where(RecordDao.Properties.Sex.eq("男"));
            } else if (TextUtils.equals(sex, "女")) {
                queryBuilder.where(RecordDao.Properties.Sex.eq("女"));
            }
        }
        if (!TextUtils.isEmpty(cardNumber)) {
            queryBuilder.where(RecordDao.Properties.CardNumber.eq(cardNumber));
        }
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            queryBuilder.where(RecordDao.Properties.VerifyTime.ge(ValueUtil.simpleDateFormat.parse(startDate).getTime()));
            queryBuilder.where(RecordDao.Properties.VerifyTime.le(ValueUtil.simpleDateFormat.parse(endDate).getTime()));
        }
        if (upload != null) {
            queryBuilder.where(RecordDao.Properties.Upload.eq(upload));
        }
        return queryBuilder.orderDesc(RecordDao.Properties.VerifyTime)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

}
