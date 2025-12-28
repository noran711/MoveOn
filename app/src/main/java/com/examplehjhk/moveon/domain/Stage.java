package com.examplehjhk.moveon.domain;

public class Stage {

    private int stageNumber;
    private int supportPercent;
    private int minRom;
    private int maxRom;
    private int obstacleCount;

    public boolean isPassed(int obstaclesCleared) {
        // Logic to check if the stage is passed
        return obstaclesCleared >= obstacleCount;
    }

    // Standard Getters and Setters
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
}
