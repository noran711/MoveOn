package com.examplehjhk.moveon.hardware;

/**
 * Interface definition for a callback to be invoked when hardware events occur.
 * This listener tracks changes from the therapy device, such as movement angles
 * and hardware slider inputs.
 */
public interface DeviceListener {

    /**
     * Called when the hardware detects a change in the user's arm or device angle.
     *
     * @param angle The current angle in degrees.
     */
    void onAngleChanged(float angle);

    /**
     * Called when the raw value of the hardware slider changes.
     *
     * @param potiRaw The raw integer value received from the physical sensor.
     */
    void onSliderChanged(int potiRaw);

    /**
     * Called when the connection to the hardware device is lost.
     */
    void onDisconnected();
}