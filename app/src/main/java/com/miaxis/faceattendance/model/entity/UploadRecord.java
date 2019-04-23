package com.miaxis.faceattendance.model.entity;

public class UploadRecord {

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
    private String mode;

    public UploadRecord() {
    }

    public UploadRecord(String cardNumber, String facePicture, String latitude, String longitude, String location, String sex, String name, String verifyTime, String score, Boolean upload, String mode) {
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
        this.mode = mode;
    }

    private UploadRecord(Builder builder) {
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
        setMode(builder.mode);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFacePicture() {
        return facePicture;
    }

    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Boolean getUpload() {
        return upload;
    }

    public void setUpload(Boolean upload) {
        this.upload = upload;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static final class Builder {
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
        private String mode;

        public Builder() {
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

        public Builder mode(String val) {
            mode = val;
            return this;
        }

        public UploadRecord build() {
            return new UploadRecord(this);
        }
    }
}
