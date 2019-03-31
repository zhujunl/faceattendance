package com.miaxis.faceattendance.contract;

import com.miaxis.faceattendance.model.entity.Record;

import java.util.List;

public interface RecordContract {
    interface View extends BaseContract.View {
        void loadRecordCallback(List<Record> recordList);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadRecord(int pageNum, int pageSize, String name, String sex, String cardNumber, Boolean upload, String startDate, String endDate);
    }
}
