package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Represents an obstacle in the game.
 * It manages position, gap generation based on patient ROM, and collision detection.
 */
public class Obstacle implements Serializable {
    // Horizontal position on the screen
    public float x;

    // Constant width of the obstacle
    public float width = 150f;

    // Vertical starting position of the gap
    public float gapY;

    // Total vertical height of the opening
    public float gapSize = 400f;

    // Flag to track if the player has successfully passed this specific obstacle
    public boolean passed = false;

    /**
     * Constructor for generating an obstacle.
     *
     * @param screenX     Starting horizontal coordinate.
     * @param screenY     Total screen height.
     * @param rom         The patient's Range of Motion (in degrees, 0-90).
     * @param birdHeight  The height of the heart character for clearance calculation.
     * @param random      Random generator for vertical variance.
     */
    public Obstacle(int screenX, int screenY, int rom, float birdHeight, Random random) {
        this.x = screenX;

        // The lowest point the heart can reach (bottom of the screen)
        float maxY = screenY;

        // Calculate the highest point allowed based on the patient's ROM
        // As ROM increases, the minY decreases (allowing the heart to fly higher)
        float minY_at_ROM = screenY - (rom / 90f * (screenY - birdHeight));

        // Determine a target Y coordinate within the patient's accessible range
        float targetHeartTipY = minY_at_ROM + random.nextFloat() * (maxY - minY_at_ROM);

        // Offset for the center of the heart character
        float heartCenterY = targetHeartTipY - (birdHeight / 2f);

        // Calculate the gap boundaries based on the calculated center
        this.gapY = heartCenterY - (gapSize / 2f);

        // Ensure the gap stays within screen boundaries
        if (gapY < 0) gapY = 0;
        if (gapY + gapSize > screenY) gapY = screenY - gapSize;
    }

    /**
     * Checks if the heart character has collided with this obstacle.
     *
     * @param bird The player's bird object.
     * @return true if a collision is detected, false otherwise.
     */
    public boolean collides(Bird bird) {
        float hitBoxWidth = bird.getHitBoxWidth();
        float scaledHeight = bird.getScaledHeight();

        float hX = bird.x;
        float hY = bird.y;

        // Check horizontal overlap between the bird and the obstacle
        if (hX + hitBoxWidth > x && hX - hitBoxWidth < x + width) {
            // Check if the bird is above the gap
            if (hY - scaledHeight < gapY) return true;

            // Check if the bird is below the gap
            if (hY > gapY + gapSize) return true;
        }

        // No collision detected
        return false;
    }
}