package com.examplehjhk.moveon.domain;

import java.util.Date;
import java.util.List;

public class Patient extends User {

    private String firstName;
    private String lastName;
    private Date birthDate;
    private String phone;
    private int initialRomMin;
    private int initialRomMax;
    private int streak;
    private int currentLevelIndex;
    private int currentStageIndex;
    private List<Patient> friends;

    @Override
    public void updatePassword(String newPassword) {
        // Logic to update password
    }

    public void incrementStreak() {
        // Logic to increment streak
    }

    public void resetStreak() {
        // Logic to reset streak
    }

    // Standard Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getInitialRomMin() {
        return initialRomMin;
    }

    public void setInitialRomMin(int initialRomMin) {
        this.initialRomMin = initialRomMin;
    }

    public int getInitialRomMax() {
        return initialRomMax;
    }

    public void setInitialRomMax(int initialRomMax) {
        this.initialRomMax = initialRomMax;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }

    public int getCurrentStageIndex() {
        return currentStageIndex;
    }

    public void setCurrentStageIndex(int currentStageIndex) {
        this.currentStageIndex = currentStageIndex;
    }

    public List<Patient> getFriends() {
        return friends;
    }

    public void setFriends(List<Patient> friends) {
        this.friends = friends;
    }
}
