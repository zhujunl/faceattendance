package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Config {

    @Id
    private Long id;
    private String uploadUrl;
    @Generated(hash = 1533449397)
    public Config(Long id, String uploadUrl) {
        this.id = id;
        this.uploadUrl = uploadUrl;
    }
    @Generated(hash = 589037648)
    public Config() {
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

}
