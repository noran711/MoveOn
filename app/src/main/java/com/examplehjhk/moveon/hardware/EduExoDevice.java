package com.examplehjhk.moveon.hardware;

public interface EduExoDevice {

    void connect();

    void disconnect();

    float readArmAngle();

    float readThumbSlider();

    void setMotorResistance(int level);

    void applyRestriction();

    void vibrationFeedback();

    int getBatteryLevel();
}
