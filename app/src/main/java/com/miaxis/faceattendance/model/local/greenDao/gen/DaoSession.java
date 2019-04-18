package com.miaxis.faceattendance.model.local.greenDao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.miaxis.faceattendance.model.entity.Config;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.Record;
import com.miaxis.faceattendance.model.entity.WhiteCard;

import com.miaxis.faceattendance.model.local.greenDao.gen.ConfigDao;
import com.miaxis.faceattendance.model.local.greenDao.gen.PersonDao;
import com.miaxis.faceattendance.model.local.greenDao.gen.RecordDao;
import com.miaxis.faceattendance.model.local.greenDao.gen.WhiteCardDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig configDaoConfig;
    private final DaoConfig personDaoConfig;
    private final DaoConfig recordDaoConfig;
    private final DaoConfig whiteCardDaoConfig;

    private final ConfigDao configDao;
    private final PersonDao personDao;
    private final RecordDao recordDao;
    private final WhiteCardDao whiteCardDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        configDaoConfig = daoConfigMap.get(ConfigDao.class).clone();
        configDaoConfig.initIdentityScope(type);

        personDaoConfig = daoConfigMap.get(PersonDao.class).clone();
        personDaoConfig.initIdentityScope(type);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        whiteCardDaoConfig = daoConfigMap.get(WhiteCardDao.class).clone();
        whiteCardDaoConfig.initIdentityScope(type);

        configDao = new ConfigDao(configDaoConfig, this);
        personDao = new PersonDao(personDaoConfig, this);
        recordDao = new RecordDao(recordDaoConfig, this);
        whiteCardDao = new WhiteCardDao(whiteCardDaoConfig, this);

        registerDao(Config.class, configDao);
        registerDao(Person.class, personDao);
        registerDao(Record.class, recordDao);
        registerDao(WhiteCard.class, whiteCardDao);
    }
    
    public void clear() {
        configDaoConfig.clearIdentityScope();
        personDaoConfig.clearIdentityScope();
        recordDaoConfig.clearIdentityScope();
        whiteCardDaoConfig.clearIdentityScope();
    }

    public ConfigDao getConfigDao() {
        return configDao;
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public WhiteCardDao getWhiteCardDao() {
        return whiteCardDao;
    }

}
