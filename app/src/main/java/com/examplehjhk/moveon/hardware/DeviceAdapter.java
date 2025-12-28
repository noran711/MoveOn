package com.examplehjhk.moveon.hardware;

import com.examplehjhk.moveon.domain.Attempt;
import com.examplehjhk.moveon.domain.GameSession;
import com.examplehjhk.moveon.domain.SensorSample;

public class DeviceAdapter implements DeviceListener {

    private Attempt activeAttempt;
    private GameSession activeSession;

    @Override
    public void onAngleChanged(float angle) {
        // Logic to create a SensorSample and add it to the active Attempt
    }

    @Override
    public void onSliderChanged(float value) {
        // Logic to handle slider changes
    }

    @Override
    public void onDisconnected() {
        // Logic to handle device disconnection
    }

    // Standard Getters and Setters
    public Attempt getActiveAttempt() {
        return activeAttempt;
    }

    public void setActiveAttempt(Attempt activeAttempt) {
        this.activeAttempt = activeAttempt;
    }

    public GameSession getActiveSession() {
        return activeSession;
    }

    public void setActiveSession(GameSession activeSession) {
        this.activeSession = activeSession;
    }
}
