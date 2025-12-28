package com.examplehjhk.moveon.domain;

public class Bird {

    private float x;
    private float y;
    private float velocity;
    private float size;

    public void updatePosition() {
        // Logic to update bird's position
    }

    public void resetPosition() {
        // Logic to reset bird's position
    }

    public void resize() {
        // Logic to resize the bird
    }

    // Standard Getters and Setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
