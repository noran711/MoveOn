package com.examplehjhk.moveon.hardware;

public class ThumbSlider {

    private float minValue;
    private float maxValue;
    private float currentValue;

    public float calculateBirdSize() {
        // Logic to calculate bird size based on slider value
        return 0.0f;
    }

    public float calculateSpeedFactor() {
        // Logic to calculate speed factor based on slider value
        return 1.0f;
    }

    // Standard Getters and Setters
    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }
}
