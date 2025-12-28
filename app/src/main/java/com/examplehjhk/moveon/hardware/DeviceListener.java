package com.examplehjhk.moveon.hardware;

public interface DeviceListener {

    void onAngleChanged(float angle);

    void onSliderChanged(float value);

    void onDisconnected();
}
