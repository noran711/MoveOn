package com.examplehjhk.moveon.hardware;

import com.examplehjhk.moveon.domain.Attempt;
import com.examplehjhk.moveon.domain.GameSession;
import com.examplehjhk.moveon.domain.SensorSample;
import com.examplehjhk.moveon.GameView;

public class DeviceAdapter implements DeviceListener {

    private final GameView gameView;

    // UML: activeAttempt/activeSession
    private GameSession activeSession;
    private Attempt activeAttempt;

    // Wir merken uns den letzten Sliderwert, damit wir beim Angle-Sample alles haben
    private int lastPotiRaw = 0;

    public DeviceAdapter(GameView gameView) {
        this.gameView = gameView;
    }

    public void setActiveSession(GameSession session, Attempt attempt) {
        this.activeSession = session;
        this.activeAttempt = attempt;
    }

    @Override
    public void onAngleChanged(float angle) {
        if (gameView != null) gameView.setArmAngle(angle);

        // Logging (UML SensorSample)
        if (activeAttempt != null) {
            float bx = (gameView != null) ? gameView.getBirdX() : 0f;
            float by = (gameView != null) ? gameView.getBirdY() : 0f;

            activeAttempt.addSample(new SensorSample(
                    System.currentTimeMillis(),
                    angle,
                    lastPotiRaw,
                    bx,
                    by
            ));
        }
    }

    @Override
    public void onSliderChanged(int potiRaw) {
        lastPotiRaw = potiRaw;
        if (gameView != null) gameView.setPotiRaw(potiRaw);
    }

    @Override
    public void onDisconnected() {
        // optional
    }
}
