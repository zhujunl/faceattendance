package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.local.greenDao.gen.PersonDao;

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
        personDao.insert(person);
    }

}
