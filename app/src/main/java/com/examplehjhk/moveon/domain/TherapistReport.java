package com.examplehjhk.moveon.domain;

import java.util.List;
import java.util.Map;

public class TherapistReport {

    private List<Integer> problemAngles;
    private Map<Integer, Integer> attemptsPerStage;
    private int trainingFrequency;
    private int currentStageIndex;
    private int currentLevelIndex;

    public TherapistReport getTherapistReport() {
        // Logic to generate the therapist report
        return this;
    }

    // Standard Getters and Setters
    public List<Integer> getProblemAngles() {
        return problemAngles;
    }

    public void setProblemAngles(List<Integer> problemAngles) {
        this.problemAngles = problemAngles;
    }

    public Map<Integer, Integer> getAttemptsPerStage() {
        return attemptsPerStage;
    }

    public void setAttemptsPerStage(Map<Integer, Integer> attemptsPerStage) {
        this.attemptsPerStage = attemptsPerStage;
    }

    public int getTrainingFrequency() {
        return trainingFrequency;
    }

    public void setTrainingFrequency(int trainingFrequency) {
        this.trainingFrequency = trainingFrequency;
    }

    public int getCurrentStageIndex() {
        return currentStageIndex;
    }

    public void setCurrentStageIndex(int currentStageIndex) {
        this.currentStageIndex = currentStageIndex;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }
}
