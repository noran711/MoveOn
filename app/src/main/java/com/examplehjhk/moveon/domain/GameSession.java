package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a single game session played by a patient.
 * This class tracks session identity, timing, and performance results.
 */
public class GameSession implements Serializable {

    // Unique identifier for the session, automatically generated upon instantiation
    public String sessionId = UUID.randomUUID().toString();

    // The unique username of the patient who played this session
    public String patientUsername;

    // Time tracking in milliseconds for calculating total duration
    public long startTimeMs;
    public long endTimeMs;

    // The total time spent in the session, converted to seconds
    public int durationSec;

    // Game progress and outcome data
    public int levelNumber;
    public int obstaclesCleared;
    public boolean success; // Indicates if the session objective was met

    /**
     * Initializes the session with the necessary starting information.
     * Records the current system time as the start time.
     *
     * @param patientUsername The user currently playing.
     * @param levelNumber The specific level being played.
     */
    public void start(String patientUsername, int levelNumber) {
        this.patientUsername = patientUsername;
        this.levelNumber = levelNumber;
        this.startTimeMs = System.currentTimeMillis();
    }

    /**
     * Finalizes the session results and calculates the total duration.
     * Records the current system time as the end time.
     *
     * @param success Whether the player successfully finished the session.
     * @param obstaclesCleared The count of obstacles bypassed by the player.
     */
    public void end(boolean success, int obstaclesCleared) {
        this.success = success;
        this.obstaclesCleared = obstaclesCleared;
        this.endTimeMs = System.currentTimeMillis();

        // Calculate duration by subtracting start time from end time and converting to seconds
        this.durationSec = (int) ((endTimeMs - startTimeMs) / 1000);
    }
}