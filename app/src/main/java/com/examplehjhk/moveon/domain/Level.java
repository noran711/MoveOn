package com.examplehjhk.moveon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Level implements Serializable {

    private int levelNumber;
    private boolean isLocked;
    private int obstacleCount;
    private List<Stage> stages;

    public Level() {
        this.levelNumber = 1;
        this.isLocked = false;
        this.obstacleCount = 30;     // Standard wie bisher
        this.stages = new ArrayList<>();
    }

    public Level(int levelNumber) {
        this();
        this.levelNumber = levelNumber;
    }

    public void unlock() {
        this.isLocked = false;
    }

    /** Minimal UML-konform: wenn du Stages noch nicht wirklich nutzt, passt das so. */
    public boolean isCompleted(Patient patient) {
        // Wenn du später Stages wirklich nutzt, kannst du hier prüfen,
        // ob patient alle stages abgeschlossen hat.
        return true;
    }

    public Stage getNextStage(int currentStageIndex) {
        if (stages == null || stages.isEmpty()) return null;
        int next = currentStageIndex + 1;
        if (next < 0 || next >= stages.size()) return null;
        return stages.get(next);
    }

    // ===== Getters & Setters =====
    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void setObstacleCount(int obstacleCount) {
        this.obstacleCount = obstacleCount;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
}
