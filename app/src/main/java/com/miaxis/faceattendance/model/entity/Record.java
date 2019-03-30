package com.miaxis.faceattendance.model.entity;

import com.miaxis.faceattendance.model.entity.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class Record {

    @Id(autoincrement = true)
    private Long id;
    private String cardNumber;
    private String facePicture;
    private String latitude;
    private String longitude;
    private String location;
    private String sex;
    private String name;
    @Convert(converter = DateConverter.class, columnType = Long.class)
    private String verifyTime;
    private String score;
    private Boolean upload;
    @Generated(hash = 1747385493)
    public Record(Long id, String cardNumber, String facePicture, String latitude,
            String longitude, String location, String sex, String name,
            String verifyTime, String score, Boolean upload) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.facePicture = facePicture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.sex = sex;
        this.name = name;
        this.verifyTime = verifyTime;
        this.score = score;
        this.upload = upload;
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
        setSex(builder.sex);
        setName(builder.name);
        setVerifyTime(builder.verifyTime);
        setScore(builder.score);
        setUpload(builder.upload);
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
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
    public Boolean getUpload() {
        return this.upload;
    }
    public void setUpload(Boolean upload) {
        this.upload = upload;
    }

    public static final class Builder {
        private Long id;
        private String cardNumber;
        private String facePicture;
        private String latitude;
        private String longitude;
        private String location;
        private String sex;
        private String name;
        private String verifyTime;
        private String score;
        private Boolean upload;

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

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
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

        public Builder upload(Boolean val) {
            upload = val;
            return this;
        }

        public Record build() {
            return new Record(this);
        }
    }
}