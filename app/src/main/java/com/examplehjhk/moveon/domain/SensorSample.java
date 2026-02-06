package com.examplehjhk.moveon.domain;

import java.io.Serializable;

public class SensorSample implements Serializable {
    public long timestampMs;
    public float armAngle;
    public int sliderRaw;
    public float birdX;
    public float birdY;

    public SensorSample(long timestampMs, float armAngle, int sliderRaw, float birdX, float birdY) {
        this.timestampMs = timestampMs;
        this.armAngle = armAngle;
        this.sliderRaw = sliderRaw;
        this.birdX = birdX;
        this.birdY = birdY;
    }
}
