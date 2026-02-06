package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.Random;

public class Obstacle implements Serializable {
    public float x;
    public float width = 150f;

    public float gapY;
    public float gapSize = 400f;

    public boolean passed = false;

    public Obstacle(int screenX, int screenY, int rom, float birdHeight, Random random) {
        this.x = screenX;

        float maxY = screenY;
        float minY_at_ROM = screenY - (rom / 90f * (screenY - birdHeight));

        float targetHeartTipY = minY_at_ROM + random.nextFloat() * (maxY - minY_at_ROM);
        float heartCenterY = targetHeartTipY - (birdHeight / 2f);

        this.gapY = heartCenterY - (gapSize / 2f);
        if (gapY < 0) gapY = 0;
        if (gapY + gapSize > screenY) gapY = screenY - gapSize;
    }

    public boolean collides(Bird bird) {
        float hitBoxWidth = bird.getHitBoxWidth();
        float scaledHeight = bird.getScaledHeight();

        float hX = bird.x;
        float hY = bird.y;

        if (hX + hitBoxWidth > x && hX - hitBoxWidth < x + width) {
            if (hY - scaledHeight < gapY) return true;
            if (hY > gapY + gapSize) return true;
        }
        return false;
    }
}
