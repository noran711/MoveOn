package com.examplehjhk.moveon.domain;

import java.util.List;

public class Level {

    private int levelNumber;
    private boolean isLocked;
    private int obstacleCount;
    private List<Stage> stages;

    public void unlock() {
        this.isLocked = false;
    }

    public boolean isCompleted(Patient patient) {
        // Logic to check if all stages are completed by the patient
        return false;
    }

    public Stage getNextStage(int currentStageIndex) {
        // Logic to get the next stage
        return null;
    }

    // Standard Getters and Setters
    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void setObstacleCount(int obstacleCount) {
        this.obstacleCount = obstacleCount;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
}
