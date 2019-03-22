package com.miaxis.faceattendance.model.entity;

import android.graphics.Bitmap;

public class IDCardRecord {

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
    /** 身份证照片 **/
    private String cardPicture;
    /** 现场人员照片 **/
    private String facePicture;
    /** 指纹0 **/
    private String fingerprint0;
    /** 指纹0指位 **/
    private String fingerprintPosition0;
    /** 指纹1 **/
    private String fingerprint1;
    /** 指纹1指位 **/
    private String fingerprintPosition1;
    /** 指纹模式下用于比对的指纹 **/
    private String localeFingerprint;

    /** 比对结果 **/
    private String result;

    /** 港澳台：通行证号码 **/
    private String passNumber;
    /** 港澳台：签发次数 **/
    private String issueCount;
    /** 外国人：中文姓名 **/
    private String chineseName;
    /** 外国人：证件版本号 **/
    private String version;

    /** 位置信息 **/
    private String location;
    /** 经度 **/
    private String longitude;
    /** 纬度 **/
    private String latitude;
    /** 比对时间 **/
    private String verifyTime;
    /** 机器名称 **/
    private String deviceName;
    /** 设备MAC地址 **/
    private String deviceMac;

    /** 身份证照片Bitmap **/
    private Bitmap cardBitmap;
    /** 身份证照片人脸特征 **/
    private byte[] cardFeature;

    public IDCardRecord() {
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

    public String getFingerprint0() {
        return fingerprint0;
    }

    public void setFingerprint0(String fingerprint0) {
        this.fingerprint0 = fingerprint0;
    }

    public String getFingerprintPosition0() {
        return fingerprintPosition0;
    }

    public void setFingerprintPosition0(String fingerprintPosition0) {
        this.fingerprintPosition0 = fingerprintPosition0;
    }

    public String getFingerprint1() {
        return fingerprint1;
    }

    public void setFingerprint1(String fingerprint1) {
        this.fingerprint1 = fingerprint1;
    }

    public String getFingerprintPosition1() {
        return fingerprintPosition1;
    }

    public void setFingerprintPosition1(String fingerprintPosition1) {
        this.fingerprintPosition1 = fingerprintPosition1;
    }

    public String getLocaleFingerprint() {
        return localeFingerprint;
    }

    public void setLocaleFingerprint(String localeFingerprint) {
        this.localeFingerprint = localeFingerprint;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public Bitmap getCardBitmap() {
        return cardBitmap;
    }

    public void setCardBitmap(Bitmap cardBitmap) {
        this.cardBitmap = cardBitmap;
    }

    public byte[] getCardFeature() {
        return cardFeature;
    }

    public void setCardFeature(byte[] cardFeature) {
        this.cardFeature = cardFeature;
    }
}
