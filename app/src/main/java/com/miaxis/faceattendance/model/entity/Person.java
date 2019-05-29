package com.miaxis.faceattendance.model.entity;

import com.miaxis.faceattendance.model.entity.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class Person {

    @Id(autoincrement = true)
    private Long id;
    /** 卡片类型 空值=二代证，J=港澳台，I=外国人永久居留证 **/
    private String cardType;
    /** 物理编号 **/
    private String cardId;
    /** 姓名 **/
    private String name;
    /** 出生日期 **/
    private String birthday;
    /** 地址 / 地址 / 空值 **/
    private String address;
    /** 身份证号码 / 身份证号 / 永久居留证号码 **/
    private String cardNumber;
    /** 签发机构 / 签发机构 / 申请受理机关代码 **/
    private String issuingAuthority;
    /** 有效期开始 **/
    private String validateStart;
    /** 有效期结束 **/
    private String validateEnd;
    /** 男/女 **/
    private String sex;
    /** 民族 / 空值 / 国籍或所在地区代码 **/
    private String nation;
    /** 人脸特征 **/
    private String faceFeature;
    /** 人脸照片 **/
    private String facePicture;
    /** 身份证照片 **/
    private String cardPicture;

    /** 港澳台：通行证号码 **/
    private String passNumber;
    /** 港澳台：签发次数 **/
    private String issueCount;
    /** 外国人：中文姓名 **/
    private String chineseName;
    /** 外国人：证件版本号 **/
    private String version;

    /** 人证核验评分 **/
    private String score;

    @Convert(converter = DateConverter.class, columnType = Long.class)
    private String registerTime;
    private long categoryId;
    @Generated(hash = 324581089)
    public Person(Long id, String cardType, String cardId, String name,
            String birthday, String address, String cardNumber,
            String issuingAuthority, String validateStart, String validateEnd,
            String sex, String nation, String faceFeature, String facePicture,
            String cardPicture, String passNumber, String issueCount,
            String chineseName, String version, String score, String registerTime,
            long categoryId) {
        this.id = id;
        this.cardType = cardType;
        this.cardId = cardId;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.cardNumber = cardNumber;
        this.issuingAuthority = issuingAuthority;
        this.validateStart = validateStart;
        this.validateEnd = validateEnd;
        this.sex = sex;
        this.nation = nation;
        this.faceFeature = faceFeature;
        this.facePicture = facePicture;
        this.cardPicture = cardPicture;
        this.passNumber = passNumber;
        this.issueCount = issueCount;
        this.chineseName = chineseName;
        this.version = version;
        this.score = score;
        this.registerTime = registerTime;
        this.categoryId = categoryId;
    }
    @Generated(hash = 1024547259)
    public Person() {
    }

    private Person(Builder builder) {
        setId(builder.id);
        setCardType(builder.cardType);
        setCardId(builder.cardId);
        setName(builder.name);
        setBirthday(builder.birthday);
        setAddress(builder.address);
        setCardNumber(builder.cardNumber);
        setIssuingAuthority(builder.issuingAuthority);
        setValidateStart(builder.validateStart);
        setValidateEnd(builder.validateEnd);
        setSex(builder.sex);
        setNation(builder.nation);
        setFaceFeature(builder.faceFeature);
        setFacePicture(builder.facePicture);
        setCardPicture(builder.cardPicture);
        setPassNumber(builder.passNumber);
        setIssueCount(builder.issueCount);
        setChineseName(builder.chineseName);
        setVersion(builder.version);
        setScore(builder.score);
        setRegisterTime(builder.registerTime);
        setCategoryId(builder.categoryId);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardType() {
        return this.cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    public String getCardId() {
        return this.cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCardNumber() {
        return this.cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getIssuingAuthority() {
        return this.issuingAuthority;
    }
    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
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
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getNation() {
        return this.nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getFaceFeature() {
        return this.faceFeature;
    }
    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }
    public String getFacePicture() {
        return this.facePicture;
    }
    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }
    public String getCardPicture() {
        return this.cardPicture;
    }
    public void setCardPicture(String cardPicture) {
        this.cardPicture = cardPicture;
    }
    public String getPassNumber() {
        return this.passNumber;
    }
    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }
    public String getIssueCount() {
        return this.issueCount;
    }
    public void setIssueCount(String issueCount) {
        this.issueCount = issueCount;
    }
    public String getChineseName() {
        return this.chineseName;
    }
    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }
    public String getVersion() {
        return this.version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getScore() {
        return this.score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public String getRegisterTime() {
        return this.registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
    public long getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public static final class Builder {
        private Long id;
        private String cardType;
        private String cardId;
        private String name;
        private String birthday;
        private String address;
        private String cardNumber;
        private String issuingAuthority;
        private String validateStart;
        private String validateEnd;
        private String sex;
        private String nation;
        private String faceFeature;
        private String facePicture;
        private String cardPicture;
        private String passNumber;
        private String issueCount;
        private String chineseName;
        private String version;
        private String score;
        private String registerTime;
        private long categoryId;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder cardType(String val) {
            cardType = val;
            return this;
        }

        public Builder cardId(String val) {
            cardId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder birthday(String val) {
            birthday = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder issuingAuthority(String val) {
            issuingAuthority = val;
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

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder nation(String val) {
            nation = val;
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

        public Builder cardPicture(String val) {
            cardPicture = val;
            return this;
        }

        public Builder passNumber(String val) {
            passNumber = val;
            return this;
        }

        public Builder issueCount(String val) {
            issueCount = val;
            return this;
        }

        public Builder chineseName(String val) {
            chineseName = val;
            return this;
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder score(String val) {
            score = val;
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

        public Person build() {
            return new Person(this);
        }
    }
}
