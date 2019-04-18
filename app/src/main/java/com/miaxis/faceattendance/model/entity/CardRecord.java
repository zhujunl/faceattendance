package com.miaxis.faceattendance.model.entity;

public class CardRecord {

    /* 注释说明：二代证 / 港澳台 / 外国人永久居留证 */
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

    /** 港澳台：通行证号码 **/
    private String passNumber;
    /** 港澳台：签发次数 **/
    private String issueCount;
    /** 外国人：中文姓名 **/
    private String chineseName;
    /** 外国人：证件版本号 **/
    private String version;

    /** 身份证照片 **/
    private String cardPicture;
    /** 现场人员照片 **/
    private String facePicture;

    /** 比对结果 **/
    private String result;
    /** 比对分数 **/
    private String score;
    /** 比对时间 **/
    private String verifyTime;

    /** 位置信息 **/
    private String location;
    /** 经度 **/
    private String longitude;
    /** 纬度 **/
    private String latitude;

    private String mode;

    public CardRecord() {
    }

    public CardRecord(String cardType, String cardId, String name, String birthday, String address, String cardNumber, String issuingAuthority, String validateStart, String validateEnd, String sex, String nation, String passNumber, String issueCount, String chineseName, String version, String cardPicture, String facePicture, String result, String score, String verifyTime, String location, String longitude, String latitude, String mode) {
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
        this.passNumber = passNumber;
        this.issueCount = issueCount;
        this.chineseName = chineseName;
        this.version = version;
        this.cardPicture = cardPicture;
        this.facePicture = facePicture;
        this.result = result;
        this.score = score;
        this.verifyTime = verifyTime;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mode = mode;
    }

    private CardRecord(Builder builder) {
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
        setPassNumber(builder.passNumber);
        setIssueCount(builder.issueCount);
        setChineseName(builder.chineseName);
        setVersion(builder.version);
        setCardPicture(builder.cardPicture);
        setFacePicture(builder.facePicture);
        setResult(builder.result);
        setScore(builder.score);
        setVerifyTime(builder.verifyTime);
        setLocation(builder.location);
        setLongitude(builder.longitude);
        setLatitude(builder.latitude);
        setMode(builder.mode);
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    public String getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(String issueCount) {
        this.issueCount = issueCount;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCardPicture() {
        return cardPicture;
    }

    public void setCardPicture(String cardPicture) {
        this.cardPicture = cardPicture;
    }

    public String getFacePicture() {
        return facePicture;
    }

    public void setFacePicture(String facePicture) {
        this.facePicture = facePicture;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static final class Builder {
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
        private String passNumber;
        private String issueCount;
        private String chineseName;
        private String version;
        private String cardPicture;
        private String facePicture;
        private String result;
        private String score;
        private String verifyTime;
        private String location;
        private String longitude;
        private String latitude;
        private String mode;

        public Builder() {
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

        public Builder cardPicture(String val) {
            cardPicture = val;
            return this;
        }

        public Builder facePicture(String val) {
            facePicture = val;
            return this;
        }

        public Builder result(String val) {
            result = val;
            return this;
        }

        public Builder score(String val) {
            score = val;
            return this;
        }

        public Builder verifyTime(String val) {
            verifyTime = val;
            return this;
        }

        public Builder location(String val) {
            location = val;
            return this;
        }

        public Builder longitude(String val) {
            longitude = val;
            return this;
        }

        public Builder latitude(String val) {
            latitude = val;
            return this;
        }

        public Builder mode(String val) {
            mode = val;
            return this;
        }

        public CardRecord build() {
            return new CardRecord(this);
        }
    }
}
