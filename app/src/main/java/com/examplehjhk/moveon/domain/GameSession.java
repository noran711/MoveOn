package com.examplehjhk.moveon.domain;

import java.util.Date;
import java.util.List;

public class GameSession {

    private String sessionId;
    private String patientId;
    private Date startTime;
    private Date endTime;
    private long duration;
    private int level;
    private int stage;
    private int obstaclesCleared;
    private List<Float> errorAngles;
    private boolean success;
    private List<Attempt> attempts;

    public void addAttempt(Attempt attempt) {
        // Logic to add an attempt
    }

    // Standard Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getObstaclesCleared() {
        return obstaclesCleared;
    }

    public void setObstaclesCleared(int obstaclesCleared) {
        this.obstaclesCleared = obstaclesCleared;
    }

    public List<Float> getErrorAngles() {
        return errorAngles;
    }

    public void setErrorAngles(List<Float> errorAngles) {
        this.errorAngles = errorAngles;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }
}
