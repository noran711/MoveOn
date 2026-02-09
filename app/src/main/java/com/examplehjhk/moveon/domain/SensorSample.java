package com.examplehjhk.moveon.domain;

import java.io.Serializable;

/**
 * Represents a single snapshot of data from sensors and the game state.
 * This is used for high-frequency tracking of the patient's performance.
 */
public class SensorSample implements Serializable {

    // The exact system time in milliseconds when this data was recorded
    public long timestampMs;

    // The angle of the user's arm in degrees
    public float armAngle;

    // The raw value coming from the slider hardware
    public int sliderRaw;

    // The horizontal position of the bird on the screen at this moment
    public float birdX;

    // The vertical position of the bird on the screen at this moment
    public float birdY;

    /**
     * Constructor to create a new sensor data record.
     *
     * @param timestampMs Current system time.
     * @param armAngle    Angle detected by the therapy device.
     * @param sliderRaw   Raw hardware input value.
     * @param birdX       Current X-coordinate of the player.
     * @param birdY       Current Y-coordinate of the player.
     */
    public SensorSample(long timestampMs, float armAngle, int sliderRaw, float birdX, float birdY) {
        this.timestampMs = timestampMs;
        this.armAngle = armAngle;
        this.sliderRaw = sliderRaw;
        this.birdX = birdX;
        this.birdY = birdY;
    }
}