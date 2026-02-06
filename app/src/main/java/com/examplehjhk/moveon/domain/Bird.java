package com.examplehjhk.moveon.domain;

import java.io.Serializable;

public class Bird implements Serializable {
    public float x;
    public float y;

    public float scale = 1.0f;
    public float baseSize = 60f;

    public Bird() {}

    public float getScaledHeight() {
        return baseSize * 2f * scale;
    }

    public float getHitBoxWidth() {
        return baseSize * scale;
    }
}
