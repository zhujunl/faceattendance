package com.miaxis.faceattendance.contract;

import com.miaxis.faceattendance.model.entity.Person;

import java.util.List;

public interface PersonContract {
    interface View extends BaseContract.View {
        void loadPersonCallback(List<Person> personList);
        void deletePersonCallback(Person person, boolean result);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadPerson(int pageNum, int pageSize);
        void deletePerson(Person person);
    }
}
