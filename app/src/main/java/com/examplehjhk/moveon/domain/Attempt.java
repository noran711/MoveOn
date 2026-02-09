package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single attempt within a game session or stage.
 * It tracks success status and stores high-frequency sensor data.
 */
public class Attempt implements Serializable {

    // The sequence number of the attempt (e.g., 1st, 2nd, 3rd try)
    public int attemptNumber;

    // Flag indicating if the specific attempt was successful
    public boolean success;

    // List of high-frequency data points recorded during the attempt
    public final List<SensorSample> sensorSamples = new ArrayList<>();

    /**
     * Constructor for creating a new attempt.
     * @param attemptNumber The numerical order of this attempt.
     */
    public Attempt(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    /**
     * Adds a new data sample recorded by the sensors to this attempt.
     * @param s The sensor sample to be recorded.
     */
    public void addSample(SensorSample s) {
        sensorSamples.add(s);
    }

}