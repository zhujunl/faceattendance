package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Config {

    @Id
    private Long id;
    private String uploadUrl;
    private String password;
    private float qualityScore;
    private float verifyScore;
    @Generated(hash = 329258956)
    public Config(Long id, String uploadUrl, String password, float qualityScore,
            float verifyScore) {
        this.id = id;
        this.uploadUrl = uploadUrl;
        this.password = password;
        this.qualityScore = qualityScore;
        this.verifyScore = verifyScore;
    }
    @Generated(hash = 589037648)
    public Config() {
    }

    private Config(Builder builder) {
        setId(builder.id);
        setUploadUrl(builder.uploadUrl);
        setPassword(builder.password);
        setQualityScore(builder.qualityScore);
        setVerifyScore(builder.verifyScore);
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

    public static final class Builder {
        private Long id;
        private String uploadUrl;
        private String password;
        private float qualityScore;
        private float verifyScore;

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

        public Config build() {
            return new Config(this);
        }
    }
}
