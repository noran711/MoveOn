package com.examplehjhk.moveon.domain;

import java.io.Serializable;

/**
 * Represents the player character (Heart) in the game.
 * Stores spatial coordinates and handles size calculations for rendering and hit detection.
 */
public class Bird implements Serializable {

    // Horizontal position on the screen
    public float x;

    // Vertical position on the screen
    public float y;

    // Multiplier for the heart's size
    public float scale = 1.0f;

    // The fundamental size of the heart before scaling is applied
    public float baseSize = 60f;

    /**
     * Default constructor for serialization and manual instantiation.
     */
    public Bird() {}

    /**
     * Calculates the total height of the heart after applying the scale.
     * This is typically used for drawing the heart on the canvas.
     *
     * @return The scaled height in pixels.
     */
    public float getScaledHeight() {
        return baseSize * 2f * scale;
    }

    /**
     * Calculates the width of the heart's collision box.
     * This is used by the collision detection system to determine if the bird hit an obstacle.
     *
     * @return The scaled width in pixels.
     */
    public float getHitBoxWidth() {
        return baseSize * scale;
    }
}