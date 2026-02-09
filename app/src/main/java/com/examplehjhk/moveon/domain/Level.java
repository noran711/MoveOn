package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game level containing multiple stages.
 * This class handles level status (locked/unlocked) and difficulty parameters.
 */
public class Level implements Serializable {

    // The unique number identifying the level
    private int levelNumber;

    // Flag to determine if the player has access to this level
    private boolean isLocked;

    // The total number of obstacles defined for this level
    private int obstacleCount;

    // A list of individual stages that make up this level
    private List<Stage> stages;

    /**
     * Default constructor.
     * Initializes a level with default values (Level 1, unlocked, 30 obstacles).
     */
    public Level() {
        this.levelNumber = 1;
        this.isLocked = false;
        this.obstacleCount = 30;     // Standard value
        this.stages = new ArrayList<>();
    }

    /**
     * Overloaded constructor to create a specific level.
     * @param levelNumber The number to assign to this level.
     */
    public Level(int levelNumber) {
        this();
        this.levelNumber = levelNumber;
    }

    /**
     * Gets the current level number.
     * @return The integer level number.
     */
    public int getLevelNumber() {
        return levelNumber;
    }
}