package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Record {

    @Id(autoincrement = true)
    private Long id;
    private String cardNumber;
    private String facePicture;
    private String latitude;
    private String longitude;
    private String location;
    private String name;
    private String result;
    private String verifyTime;
    private String score;
    @Generated(hash = 491457769)
    public Record(Long id, String cardNumber, String facePicture, String latitude,
            String longitude, String location, String name, String result,
            String verifyTime, String score) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.facePicture = facePicture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.name = name;
        this.result = result;
        this.verifyTime = verifyTime;
        this.score = score;
    }
    @Generated(hash = 477726293)
    public Record() {
    }

    private Record(Builder builder) {
        setId(builder.id);
        setCardNumber(builder.cardNumber);
        setFacePicture(builder.facePicture);
        setLatitude(builder.latitude);
        setLongitude(builder.longitude);
        setLocation(builder.location);
        setName(builder.name);
        setResult(builder.result);
        setVerifyTime(builder.verifyTime);
        setScore(builder.score);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardNumber() {
        return this.cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getFacePicture() {
        return this.facePicture;
    }
    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }
    public String getLatitude() {
        return this.latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return this.longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getResult() {
        return this.result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getVerifyTime() {
        return this.verifyTime;
    }
    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }
    public String getScore() {
        return this.score;
    }
    public void setScore(String score) {
        this.score = score;
    }

    public static final class Builder {
        private Long id;
        private String cardNumber;
        private String facePicture;
        private String latitude;
        private String longitude;
        private String location;
        private String name;
        private String result;
        private String verifyTime;
        private String score;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder facePicture(String val) {
            facePicture = val;
            return this;
        }

        public Builder latitude(String val) {
            latitude = val;
            return this;
        }

        public Builder longitude(String val) {
            longitude = val;
            return this;
        }

        public Builder location(String val) {
            location = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder result(String val) {
            result = val;
            return this;
        }

        public Builder verifyTime(String val) {
            verifyTime = val;
            return this;
        }

        public Builder score(String val) {
            score = val;
            return this;
        }

        public Record build() {
            return new Record(this);
        }
    }
}
