package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Config {

    @Id
    private Long id;
    private String uploadUrl;
    private String cardUploadUrl;
    private String password;
    private float qualityScore;
    private float verifyScore;
    private float cardVerifyScore;
    private int recordClearThreshold;
    @Generated(hash = 279804431)
    public Config(Long id, String uploadUrl, String cardUploadUrl, String password,
            float qualityScore, float verifyScore, float cardVerifyScore,
            int recordClearThreshold) {
        this.id = id;
        this.uploadUrl = uploadUrl;
        this.cardUploadUrl = cardUploadUrl;
        this.password = password;
        this.qualityScore = qualityScore;
        this.verifyScore = verifyScore;
        this.cardVerifyScore = cardVerifyScore;
        this.recordClearThreshold = recordClearThreshold;
    }
    @Generated(hash = 589037648)
    public Config() {
    }

    private Config(Builder builder) {
        setId(builder.id);
        setUploadUrl(builder.uploadUrl);
        setCardUploadUrl(builder.cardUploadUrl);
        setPassword(builder.password);
        setQualityScore(builder.qualityScore);
        setVerifyScore(builder.verifyScore);
        setCardVerifyScore(builder.cardVerifyScore);
        setRecordClearThreshold(builder.recordClearThreshold);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUploadUrl() {
        return this.uploadUrl;
    }
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
    public String getCardUploadUrl() {
        return this.cardUploadUrl;
    }
    public void setCardUploadUrl(String cardUploadUrl) {
        this.cardUploadUrl = cardUploadUrl;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public float getQualityScore() {
        return this.qualityScore;
    }
    public void setQualityScore(float qualityScore) {
        this.qualityScore = qualityScore;
    }
    public float getVerifyScore() {
        return this.verifyScore;
    }
    public void setVerifyScore(float verifyScore) {
        this.verifyScore = verifyScore;
    }
    public float getCardVerifyScore() {
        return this.cardVerifyScore;
    }
    public void setCardVerifyScore(float cardVerifyScore) {
        this.cardVerifyScore = cardVerifyScore;
    }
    public int getRecordClearThreshold() {
        return this.recordClearThreshold;
    }
    public void setRecordClearThreshold(int recordClearThreshold) {
        this.recordClearThreshold = recordClearThreshold;
    }

    public static final class Builder {
        private Long id;
        private String uploadUrl;
        private String cardUploadUrl;
        private String password;
        private float qualityScore;
        private float verifyScore;
        private float cardVerifyScore;
        private int recordClearThreshold;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder uploadUrl(String val) {
            uploadUrl = val;
            return this;
        }

        public Builder cardUploadUrl(String val) {
            cardUploadUrl = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder qualityScore(float val) {
            qualityScore = val;
            return this;
        }

        public Builder verifyScore(float val) {
            verifyScore = val;
            return this;
        }

        public Builder cardVerifyScore(float val) {
            cardVerifyScore = val;
            return this;
        }

        public Builder recordClearThreshold(int val) {
            recordClearThreshold = val;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
