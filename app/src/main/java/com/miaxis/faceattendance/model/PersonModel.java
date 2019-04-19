package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.local.greenDao.gen.PersonDao;
import com.miaxis.faceattendance.util.FileUtil;
import com.miaxis.faceattendance.util.ValueUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

public class PersonModel {

    public static Person getPersonByCardNumber(String cardNumber) {
        return DaoManager.getInstance().getDaoSession().getPersonDao().queryBuilder()
                .where(PersonDao.Properties.CardNumber.eq(cardNumber))
                .unique();
    }

    public static synchronized void savePerson(Person person) {
        Person old = DaoManager.getInstance().getDaoSession().getPersonDao().queryBuilder()
                .where(PersonDao.Properties.CardNumber.eq(person.getCardNumber()))
                .unique();
        if (old != null) {
            deletePerson(old);
        }
        person.setRegisterTime(ValueUtil.simpleDateFormat.format(new Date()));
        DaoManager.getInstance().getDaoSession().getPersonDao().insert(person);
    }

    public static List<Person> loadPersonList(int pageNum, int pageSize) {
        return DaoManager.getInstance().getDaoSession().getPersonDao().queryBuilder()
                .orderDesc(PersonDao.Properties.RegisterTime)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

    public static List<Person> loadAllPerson() {
        return DaoManager.getInstance().getDaoSession().getPersonDao().loadAll();
    }

    public static void deletePerson(Person person) {
        FileUtil.deletefile(person.getFacePicture());
        DaoManager.getInstance().getDaoSession().getPersonDao().delete(person);
    }

    public static void deletePersonByCardNumber(String cardNumber) {
        Person person = getPersonByCardNumber(cardNumber);
        if (person != null) {
            deletePerson(person);
        }
    }

    public static long getPersonCount() {
        return DaoManager.getInstance().getDaoSession().getPersonDao().count();
    }

    public static void clearPerson() {
        DaoManager.getInstance().getDaoSession().getPersonDao().deleteAll();
        FileUtil.deleteDirectoryFile(new File(FileUtil.FACE_IMG_PATH));
    }

}
