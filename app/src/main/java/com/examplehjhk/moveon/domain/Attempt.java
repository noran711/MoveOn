package com.examplehjhk.moveon.domain;

import java.util.List;

public class Attempt {

    private int attemptNumber;
    private List<SensorSample> sensorSamples;
    private boolean success;

    public float getErrorAngle() {
        // Logic to get the error angle
        return 0.0f;
    }

    // Standard Getters and Setters
    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public List<SensorSample> getSensorSamples() {
        return sensorSamples;
    }

    public void setSensorSamples(List<SensorSample> sensorSamples) {
        this.sensorSamples = sensorSamples;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
