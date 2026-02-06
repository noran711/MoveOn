package com.examplehjhk.moveon.hardware;

public class ThumbSlider {

    private float value01 = 0f; // 0..1

    public void setFromRaw(int raw, int maxRaw) {
        if (maxRaw <= 0) maxRaw = 1;
        float v = raw / (float) maxRaw;
        value01 = Math.max(0f, Math.min(1f, v));
    }

    public void setValue01(float value01) {
        this.value01 = Math.max(0f, Math.min(1f, value01));
    }

    public float getValue01() {
        return value01;
    }
}
