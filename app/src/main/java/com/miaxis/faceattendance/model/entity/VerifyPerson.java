package com.miaxis.faceattendance.model.entity;

public class VerifyPerson {

    private String cardNumber;
    private String name;
    private String facePicturePath;
    private String time;

    public VerifyPerson(String cardNumber, String name, String facePicturePath, String time) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.facePicturePath = facePicturePath;
        this.time = time;
    }

    private VerifyPerson(Builder builder) {
        setCardNumber(builder.cardNumber);
        setName(builder.name);
        setFacePicturePath(builder.facePicturePath);
        setTime(builder.time);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacePicturePath() {
        return facePicturePath;
    }

    public void setFacePicturePath(String facePicturePath) {
        this.facePicturePath = facePicturePath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static final class Builder {
        private String cardNumber;
        private String name;
        private String facePicturePath;
        private String time;

        public Builder() {
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder facePicturePath(String val) {
            facePicturePath = val;
            return this;
        }

        public Builder time(String val) {
            time = val;
            return this;
        }

        public VerifyPerson build() {
            return new VerifyPerson(this);
        }
    }
}
