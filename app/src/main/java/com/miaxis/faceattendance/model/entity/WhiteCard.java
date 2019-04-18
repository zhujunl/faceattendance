package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WhiteCard {

    @Id
    private String cardNumber;
    private String name;
    @Generated(hash = 394163690)
    public WhiteCard(String cardNumber, String name) {
        this.cardNumber = cardNumber;
        this.name = name;
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
   
}
