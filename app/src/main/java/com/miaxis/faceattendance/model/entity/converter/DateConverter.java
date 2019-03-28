package com.miaxis.faceattendance.model.entity.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter implements PropertyConverter<String, Long> {

    @Override
    public String convertToEntityProperty(Long databaseValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return format.format(new Date(databaseValue));
    }

    @Override
    public Long convertToDatabaseValue(String entityProperty) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Long result;
        try {
            Date date = format.parse(entityProperty);
            result = date.getTime();
        } catch (ParseException e) {
            result = 0L;
        }
        return result;
    }
}