package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.local.greenDao.gen.PersonDao;
import com.miaxis.faceattendance.util.ValueUtil;

import java.util.Date;
import java.util.List;

public class PersonModel {

    public static boolean checkPersonByCardNumber(String cardNumber) {
        Person person = DaoManager.getInstance().getDaoSession().getPersonDao().queryBuilder()
                .where(PersonDao.Properties.CardNumber.eq(cardNumber))
                .unique();
        return person == null;
    }

    public static void savePerson(Person person) {
        PersonDao personDao = DaoManager.getInstance().getDaoSession().getPersonDao();
        Person old = personDao.queryBuilder()
                .where(PersonDao.Properties.CardNumber.eq(person.getCardNumber()))
                .unique();
        if (old != null) {
            personDao.delete(old);
        }
        person.setRegisterTime(ValueUtil.simpleDateFormat.format(new Date()));
        personDao.insert(person);
    }

    public static List<Person> loadPersonList(int pageNum, int pageSize) {
        return DaoManager.getInstance().getDaoSession().getPersonDao().queryBuilder()
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .list();
    }

    public static List<Person> loadAllPerson() {
        return DaoManager.getInstance().getDaoSession().getPersonDao().loadAll();
    }

    public static void deletePerson(Person person) {
        DaoManager.getInstance().getDaoSession().getPersonDao().delete(person);
    }

}
