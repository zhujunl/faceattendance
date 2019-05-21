package com.miaxis.faceattendance.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Category {

    @Id
    private Long id;
    private String categoryName;
    private String categoryPrompt;
    private String registerTime;
    @Generated(hash = 195957264)
    public Category(Long id, String categoryName, String categoryPrompt,
            String registerTime) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryPrompt = categoryPrompt;
        this.registerTime = registerTime;
    }
    @Generated(hash = 1150634039)
    public Category() {
    }

    private Category(Builder builder) {
        setId(builder.id);
        setCategoryName(builder.categoryName);
        setCategoryPrompt(builder.categoryPrompt);
        setRegisterTime(builder.registerTime);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCategoryName() {
        return this.categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryPrompt() {
        return this.categoryPrompt;
    }
    public void setCategoryPrompt(String categoryPrompt) {
        this.categoryPrompt = categoryPrompt;
    }
    public String getRegisterTime() {
        return this.registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public static final class Builder {
        private Long id;
        private String categoryName;
        private String categoryPrompt;
        private String registerTime;

        public Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder categoryName(String val) {
            categoryName = val;
            return this;
        }

        public Builder categoryPrompt(String val) {
            categoryPrompt = val;
            return this;
        }

        public Builder registerTime(String val) {
            registerTime = val;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
