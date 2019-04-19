package com.miaxis.faceattendance.model.entity;

import com.miaxis.faceattendance.model.entity.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WhiteCard {

    @Id
    private String cardNumber;
    private String name;
    @Convert(converter = DateConverter.class, columnType = Long.class)
    private String registerTime;
    @Generated(hash = 1455522599)
    public WhiteCard(String cardNumber, String name, String registerTime) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.registerTime = registerTime;
    }
    @Generated(hash = 2087748647)
    public WhiteCard() {
    }
    public String getCardNumber() {
        return this.cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRegisterTime() {
        return this.registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}
