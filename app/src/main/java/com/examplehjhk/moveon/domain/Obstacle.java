package com.examplehjhk.moveon.domain;

public class Obstacle {

    private int obstacleNumber;
    private float x;
    private float gapCenterY;
    private float width;
    private float gapSize;
    private float gabROM;
    private boolean passed;

    public boolean isOffScreen() {
        // Logic to check if the obstacle is off-screen
        return x + width < 0;
    }

    public boolean checkCollision(Bird bird) {
        // Logic to check for collision with the bird
        return false;
    }

    // Standard Getters and Setters
    public int getObstacleNumber() {
        return obstacleNumber;
    }

    public void setObstacleNumber(int obstacleNumber) {
        this.obstacleNumber = obstacleNumber;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getGapCenterY() {
        return gapCenterY;
    }

    public void setGapCenterY(float gapCenterY) {
        this.gapCenterY = gapCenterY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getGapSize() {
        return gapSize;
    }

    public void setGapSize(float gapSize) {
        this.gapSize = gapSize;
    }

    public float getGabROM() {
        return gabROM;
    }

    public void setGabROM(float gabROM) {
        this.gabROM = gabROM;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
