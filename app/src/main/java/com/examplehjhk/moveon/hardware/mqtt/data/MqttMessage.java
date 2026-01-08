package com.examplehjhk.moveon.hardware.mqtt.data;

public class MqttMessage {
    private float armAngle;
    private float sliderValue;

    public MqttMessage(float armAngle, float sliderValue) {
        this.armAngle = armAngle;
        this.sliderValue = sliderValue;
    }

    public float getArmAngle() {
        return armAngle;
    }

    public float getSliderValue() {
        return sliderValue;
    }
}
