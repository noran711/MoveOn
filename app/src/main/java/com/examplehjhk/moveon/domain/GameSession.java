package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.UUID;

public class GameSession implements Serializable {

    public String sessionId = UUID.randomUUID().toString();

    // ✅ Zuordnung zum Patienten (Therapeut sieht alles)
    public String patientUsername;

    public long startTimeMs;
    public long endTimeMs;
    public int durationSec;

    public int levelNumber;
    public int obstaclesCleared;
    public boolean success;

    public void start(String patientUsername, int levelNumber) {
        this.patientUsername = patientUsername;
        this.levelNumber = levelNumber;
        this.startTimeMs = System.currentTimeMillis();
    }

    public void end(boolean success, int obstaclesCleared) {
        this.success = success;
        this.obstaclesCleared = obstaclesCleared;
        this.endTimeMs = System.currentTimeMillis();
        this.durationSec = (int) ((endTimeMs - startTimeMs) / 1000);
    }
}
