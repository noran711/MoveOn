package com.examplehjhk.moveon.domain;

import java.util.Date;

public class SensorSample {

    private Date timestamp;
    private float armAngle;
    private float sliderValue;
    private float birdX;
    private float birdY;

    // Standard Getters and Setters
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getArmAngle() {
        return armAngle;
    }

    public void setArmAngle(float armAngle) {
        this.armAngle = armAngle;
    }

    public float getSliderValue() {
        return sliderValue;
    }

    public void setSliderValue(float sliderValue) {
        this.sliderValue = sliderValue;
    }

    public float getBirdX() {
        return birdX;
    }

    public void setBirdX(float birdX) {
        this.birdX = birdX;
    }

    public float getBirdY() {
        return birdY;
    }

    public void setBirdY(float birdY) {
        this.birdY = birdY;
    }
}
