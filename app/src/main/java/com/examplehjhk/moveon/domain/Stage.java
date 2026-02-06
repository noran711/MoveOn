package com.examplehjhk.moveon.domain;

import java.io.Serializable;

public class Stage implements Serializable {

    private int stageNumber;

    /** Unterstützungsgrad in Prozent (z.B. 10 = 10 %) */
    private int supportPercent;

    /** Minimaler erlaubter ROM-Wert (Grad) */
    private int minRom;

    /** Maximaler erlaubter ROM-Wert (Grad) */
    private int maxRom;

    /** Anzahl der Hindernisse, die in dieser Stage überwunden werden müssen */
    private int obstacleCount;

    /** Status */
    private boolean completed = false;

    // ===== Konstruktoren =====

    /** Leerer Konstruktor (z.B. für Serialisierung) */
    public Stage() {}

    /** Voller Konstruktor (empfohlen) */
    public Stage(int stageNumber,
                 int supportPercent,
                 int minRom,
                 int maxRom,
                 int obstacleCount) {

        this.stageNumber = stageNumber;
        this.supportPercent = supportPercent;
        this.minRom = minRom;
        this.maxRom = maxRom;
        this.obstacleCount = obstacleCount;
    }

    // ===== Logik =====

    /** Prüft, ob die Stage bestanden wurde */
    public boolean isPassed(int obstaclesCleared) {
        return obstaclesCleared >= obstacleCount;
    }

    /** Markiert die Stage als abgeschlossen */
    public void markCompleted() {
        this.completed = true;
    }

    /** Prüft, ob ein ROM-Wert innerhalb der Stage-Grenzen liegt */
    public boolean isRomAllowed(int rom) {
        return rom >= minRom && rom <= maxRom;
    }

    // ===== Getter / Setter =====

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public int getSupportPercent() {
        return supportPercent;
    }

    public void setSupportPercent(int supportPercent) {
        this.supportPercent = supportPercent;
    }

    public int getMinRom() {
        return minRom;
    }

    public void setMinRom(int minRom) {
        this.minRom = minRom;
    }

    public int getMaxRom() {
        return maxRom;
    }

    public void setMaxRom(int maxRom) {
        this.maxRom = maxRom;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }

    public void setObstacleCount(int obstacleCount) {
        this.obstacleCount = obstacleCount;
    }

    public boolean isCompleted() {
        return completed;
    }
}
