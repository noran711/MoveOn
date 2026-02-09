package com.examplehjhk.moveon.domain;

import java.io.Serializable;

/**
 * Represents a specific stage within a game level.
 * Defines the parameters for the therapy game, such as support levels and ROM range.
 */
public class Stage implements Serializable {

    private int stageNumber;

    /** Degree of support in percent */
    private int supportPercent;

    /** Minimum allowed ROM value in degrees */
    private int minRom;

    /** Maximum allowed ROM value in degrees */
    private int maxRom;

    /** Number of obstacles that must be overcome to pass this stage */
    private int obstacleCount;

    /** Completion status of the stage */
    private boolean completed = false;

    // ===== Constructors =====

    /**
     * Default constructor
     */
    public Stage() {}

    /**
     * Full constructor
     *
     * @param stageNumber    The sequence number of the stage
     * @param supportPercent The assistance level provided to the patient
     * @param minRom         The minimum Range of Motion required
     * @param maxRom         The maximum Range of Motion required
     * @param obstacleCount  Number of obstacles to be cleared
     */
    public Stage(int stageNumber,
                 int supportPercent,
                 int minRom,
                 int maxRom,
                 int obstacleCount) {

        this.stageNumber = stageNumber;
        this.supportPercent = supportPercent;
        this.minRom = minRom;
        this.maxRom = maxRom;
        this.obstacleCount = obstacleCount;
    }



    /**
     * Checks if the stage has been passed based on the number of obstacles cleared.
     *
     * @param obstaclesCleared The count of obstacles the player successfully passed
     * @return true if the cleared count meets or exceeds the required count
     */
    public boolean isPassed(int obstaclesCleared) {
        return obstaclesCleared >= obstacleCount;
    }

    /**
     * Marks the stage as successfully completed.
     */
    public void markCompleted() {
        this.completed = true;
    }

    /**
     * Checks if a specific ROM value lies within the allowed boundaries of this stage.
     *
     * @param rom The Range of Motion value to check
     * @return true if the value is between minRom and maxRom
     */
    public boolean isRomAllowed(int rom) {
        return rom >= minRom && rom <= maxRom;
    }


    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public int getSupportPercent() {
        return supportPercent;
    }

    public void setSupportPercent(int supportPercent) {
        this.supportPercent = supportPercent;
    }

    public int getMinRom() {
        return minRom;
    }

    public void setMinRom(int minRom) {
        this.minRom = minRom;
    }

    public int getMaxRom() {
        return maxRom;
    }

    public void setMaxRom(int maxRom) {
        this.maxRom = maxRom;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void setObstacleCount(int obstacleCount) {
        this.obstacleCount = obstacleCount;
    }

    public boolean isCompleted() {
        return completed;
    }
}