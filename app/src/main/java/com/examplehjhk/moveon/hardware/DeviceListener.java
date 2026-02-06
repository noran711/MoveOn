package com.examplehjhk.moveon.hardware;

public interface DeviceListener {
    void onAngleChanged(float angle);
    void onSliderChanged(int potiRaw);
    void onDisconnected();
}
