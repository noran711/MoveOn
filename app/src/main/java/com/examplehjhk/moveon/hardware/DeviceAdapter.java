package com.examplehjhk.moveon.hardware;

import com.examplehjhk.moveon.domain.Attempt;
import com.examplehjhk.moveon.domain.GameSession;
import com.examplehjhk.moveon.domain.SensorSample;
import com.examplehjhk.moveon.GameView;

/**
 * Acts as a bridge between the hardware sensors and the game logic.
 * It implements DeviceListener to react to angle and slider changes,
 * updating the UI and logging data for therapy analysis.
 */
public class DeviceAdapter implements DeviceListener {

    // Reference to the custom view that renders the game
    private final GameView gameView;

    private GameSession activeSession;
    private Attempt activeAttempt;

    // Cache for the most recent slider value to include it in angle-based samples
    private int lastPotiRaw = 0;

    /**
     * Constructor for the DeviceAdapter.
     * @param gameView The view where the bird and obstacles are rendered.
     */
    public DeviceAdapter(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * Links the adapter to a specific session and attempt for data recording.
     * @param session The current therapy session.
     * @param attempt The current specific try/attempt within that session.
     */
    public void setActiveSession(GameSession session, Attempt attempt) {
        this.activeSession = session;
        this.activeAttempt = attempt;
    }

    /**
     * Triggered when the hardware detects a change in the arm's angle.
     * Updates the game view and records a high-frequency sensor sample.
     *
     * @param angle The new angle in degrees.
     */
    @Override
    public void onAngleChanged(float angle) {
        // Update the visual representation of the arm/bird in the game
        if (gameView != null) {
            gameView.setArmAngle(angle);
        }

        // Data Logging: Create a new SensorSample if an attempt is active
        if (activeAttempt != null) {
            // Retrieve current bird coordinates for spatial tracking
            float bx = (gameView != null) ? gameView.getBirdX() : 0f;
            float by = (gameView != null) ? gameView.getBirdY() : 0f;

            // Log the timestamp, angle, hardware input, and bird position
            activeAttempt.addSample(new SensorSample(
                    System.currentTimeMillis(),
                    angle,
                    lastPotiRaw,
                    bx,
                    by
            ));
        }
    }

    /**
     * Triggered when the hardware slider value changes.
     *
     * @param potiRaw The raw integer value from the hardware.
     */
    @Override
    public void onSliderChanged(int potiRaw) {
        this.lastPotiRaw = potiRaw;
        // Pass the raw value to the game view
        if (gameView != null) {
            gameView.setPotiRaw(potiRaw);
        }
    }

    /**
     * Triggered when the connection to the hardware device is lost.
     */
    @Override
    public void onDisconnected() {
        // Future implementation: Handle reconnection logic or pause the game
    }
}