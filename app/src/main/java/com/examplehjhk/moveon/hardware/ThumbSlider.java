package com.examplehjhk.moveon.hardware;

/**
 * Represents a physical thumb slider or potentiometer hardware component.
 * This class normalizes raw hardware input into a standard0.0 to 1.0 range.
 */
public class ThumbSlider {

    // Internal value representing the slider position, normalized between 0.0 and 1.0
    private float value01 = 0f;

    /**
     * Updates the slider state based on raw hardware values.
     *
     * @param raw    The current raw sensor reading from the hardware.
     * @param maxRaw The maximum expected value from the sensor.
     */
    public void setFromRaw(int raw, int maxRaw) {
        // Prevent division by zero errors
        if (maxRaw <= 0) maxRaw = 1;

        // Calculate the percentage of the current value relative to the maximum
        float v = raw / (float) maxRaw;

        // Clamp the result between 0.0 and 1.0 to ensure valid internal state
        value01 = Math.max(0f, Math.min(1f, v));
    }

    /**
     * Manually sets the normalized slider value.
     *
     * @param value01 The target value (will be clamped between 0.0 and 1.0).
     */
    public void setValue01(float value01) {
        this.value01 = Math.max(0f, Math.min(1f, value01));
    }

    /**
     * Retrieves the current normalized position of the slider.
     *
     * @return A float value between 0.0 (minimum) and 1.0 (maximum).
     */
    public float getValue01() {
        return value01;
    }
}