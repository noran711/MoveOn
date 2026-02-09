package com.examplehjhk.moveon.domain;

import java.util.List;
import java.util.Map;

/**
 * Represents a summary report generated for a therapist.
 * This class compiles analytical data about a patient's performance,
 * such as difficulty areas and progress tracking.
 */
public class TherapistReport {

    // List of ROM angles where the patient struggled
    private List<Integer> problemAngles;

    // Tracks the number of tries/attempts made on each specific stage
    private Map<Integer, Integer> attemptsPerStage;

    // Records how often the patient has performed training sessions
    private int trainingFrequency;

    // The index of the stage the patient is currently working on
    private int currentStageIndex;

    // The index of the level the patient is currently working on
    private int currentLevelIndex;

    /**
     * Factory/Utility method to retrieve or generate the report data.
     *
     * @return The current TherapistReport instance.
     */
    public TherapistReport getTherapistReport() {
        // Logic to generate or populate the therapist report would go here
        return this;
    }

    /**
     * @return A list of angles where the patient encountered difficulties.
     */
    public List<Integer> getProblemAngles() {
        return problemAngles;
    }

    public void setProblemAngles(List<Integer> problemAngles) {
        this.problemAngles = problemAngles;
    }

    /**
     * @return A map where the key is the stage number and the value is the attempt count.
     */
    public Map<Integer, Integer> getAttemptsPerStage() {
        return attemptsPerStage;
    }

    public void setAttemptsPerStage(Map<Integer, Integer> attemptsPerStage) {
        this.attemptsPerStage = attemptsPerStage;
    }

    /**
     * @return The frequency of training sessions recorded.
     */
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