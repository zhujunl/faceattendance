package com.miaxis.faceattendance.model.entity;

public class UploadPerson {

    private String name;
    private String sex;
    private String cardNumber;
    private String nation;
    private String address;
    private String validateStart;
    private String validateEnd;
    private String issuingAuthority;
    private String birthday;
    private String cardId;
    private String faceFeature;
    private String facePicture;
    private String registerTime;
    private long categoryId;
    private String mode;
    private String deviceId;

    public UploadPerson() {
    }

    public UploadPerson(String name, String sex, String cardNumber, String nation, String address, String validateStart, String validateEnd, String issuingAuthority, String birthday, String cardId, String faceFeature, String facePicture, String registerTime, long categoryId, String mode, String deviceId) {
        this.name = name;
        this.sex = sex;
        this.cardNumber = cardNumber;
        this.nation = nation;
        this.address = address;
        this.validateStart = validateStart;
        this.validateEnd = validateEnd;
        this.issuingAuthority = issuingAuthority;
        this.birthday = birthday;
        this.cardId = cardId;
        this.faceFeature = faceFeature;
        this.facePicture = facePicture;
        this.registerTime = registerTime;
        this.categoryId = categoryId;
        this.mode = mode;
        this.deviceId = deviceId;
    }

    private UploadPerson(Builder builder) {
        setName(builder.name);
        setSex(builder.sex);
        setCardNumber(builder.cardNumber);
        setNation(builder.nation);
        setAddress(builder.address);
        setValidateStart(builder.validateStart);
        setValidateEnd(builder.validateEnd);
        setIssuingAuthority(builder.issuingAuthority);
        setBirthday(builder.birthday);
        setCardId(builder.cardId);
        setFaceFeature(builder.faceFeature);
        setFacePicture(builder.facePicture);
        setRegisterTime(builder.registerTime);
        setCategoryId(builder.categoryId);
        setMode(builder.mode);
        setDeviceId(builder.deviceId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValidateStart() {
        return validateStart;
    }

    public void setValidateStart(String validateStart) {
        this.validateStart = validateStart;
    }

    public String getValidateEnd() {
        return validateEnd;
    }

    public void setValidateEnd(String validateEnd) {
        this.validateEnd = validateEnd;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getFacePicture() {
        return facePicture;
    }

    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public static final class Builder {
        private String name;
        private String sex;
        private String cardNumber;
        private String nation;
        private String address;
        private String validateStart;
        private String validateEnd;
        private String issuingAuthority;
        private String birthday;
        private String cardId;
        private String faceFeature;
        private String facePicture;
        private String registerTime;
        private long categoryId;
        private String mode;
        private String deviceId;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder nation(String val) {
            nation = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder validateStart(String val) {
            validateStart = val;
            return this;
        }

        public Builder validateEnd(String val) {
            validateEnd = val;
            return this;
        }

        public Builder issuingAuthority(String val) {
            issuingAuthority = val;
            return this;
        }

        public Builder birthday(String val) {
            birthday = val;
            return this;
        }

        public Builder cardId(String val) {
            cardId = val;
            return this;
        }

        public Builder faceFeature(String val) {
            faceFeature = val;
            return this;
        }

        public Builder facePicture(String val) {
            facePicture = val;
            return this;
        }

        public Builder registerTime(String val) {
            registerTime = val;
            return this;
        }

        public Builder categoryId(long val) {
            categoryId = val;
            return this;
        }

        public Builder mode(String val) {
            mode = val;
            return this;
        }

        public Builder deviceId(String val) {
            deviceId = val;
            return this;
        }

        public UploadPerson build() {
            return new UploadPerson(this);
        }
    }
}
