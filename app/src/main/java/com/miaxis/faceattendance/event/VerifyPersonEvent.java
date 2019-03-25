package com.miaxis.faceattendance.event;

import com.miaxis.faceattendance.model.entity.Person;
import com.miaxis.faceattendance.model.entity.RGBImage;

public class VerifyPersonEvent {

    private Person person;
    private RGBImage rgbImage;
    private float score;

    public VerifyPersonEvent(Person person, RGBImage rgbImage, float score) {
        this.person = person;
        this.rgbImage = rgbImage;
        this.score = score;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public RGBImage getRgbImage() {
        return rgbImage;
    }

    public void setRgbImage(RGBImage rgbImage) {
        this.rgbImage = rgbImage;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
