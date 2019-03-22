package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Person {

    @Id(autoincrement = true)
    private Long id;
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
    private String feature;
    private String facePicturePath;
    @Generated(hash = 1368843380)
    public Person(Long id, String name, String sex, String cardNumber,
            String nation, String address, String validateStart, String validateEnd,
            String issuingAuthority, String birthday, String cardId, String feature,
            String facePicturePath) {
        this.id = id;
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
        this.feature = feature;
        this.facePicturePath = facePicturePath;
    }
    @Generated(hash = 1024547259)
    public Person() {
    }

    private Person(Builder builder) {
        setId(builder.id);
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
        setFeature(builder.feature);
        setFacePicturePath(builder.facePicturePath);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getCardNumber() {
        return this.cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getNation() {
        return this.nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getValidateStart() {
        return this.validateStart;
    }
    public void setValidateStart(String validateStart) {
        this.validateStart = validateStart;
    }
    public String getValidateEnd() {
        return this.validateEnd;
    }
    public void setValidateEnd(String validateEnd) {
        this.validateEnd = validateEnd;
    }
    public String getIssuingAuthority() {
        return this.issuingAuthority;
    }
    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getCardId() {
        return this.cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public String getFeature() {
        return this.feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public String getFacePicturePath() {
        return this.facePicturePath;
    }
    public void setFacePicturePath(String facePicturePath) {
        this.facePicturePath = facePicturePath;
    }

    public static final class Builder {
        private Long id;
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
        private String feature;
        private String facePicturePath;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
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

        public Builder feature(String val) {
            feature = val;
            return this;
        }

        public Builder facePicturePath(String val) {
            facePicturePath = val;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
