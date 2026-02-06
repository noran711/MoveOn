package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession implements Serializable {
    public String sessionId = UUID.randomUUID().toString();
    public int patientId; // wenn du später DB nutzt; aktuell 0 ok

    public long startTimeMs;
    public long endTimeMs;
    public int durationSec;

    public int levelNumber;
    public int stageIndex; // optional
    public int obstaclesCleared;
    public boolean success;

    public final List<Attempt> attempts = new ArrayList<>();

    public void start(int levelNumber) {
        this.levelNumber = levelNumber;
        this.startTimeMs = System.currentTimeMillis();
    }

    public void end(boolean success, int obstaclesCleared) {
        this.success = success;
        this.obstaclesCleared = obstaclesCleared;
        this.endTimeMs = System.currentTimeMillis();
        this.durationSec = (int) ((endTimeMs - startTimeMs) / 1000);
    }

    public void addAttempt(Attempt a) {
        attempts.add(a);
    }
}
