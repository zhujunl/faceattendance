package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Config;

public class ConfigModel {

    public static void saveConfig(Config config) {
        DaoManager.getInstance().getDaoSession().getConfigDao().deleteByKey(1L);
        DaoManager.getInstance().getDaoSession().getConfigDao().insert(config);
    }

}
