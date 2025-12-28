package com.examplehjhk.moveon.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Therapist extends User {

    private String firstName;
    private String lastName;
    private String clinicName;
    private Date birthDate;
    private String phone;
    private List<Patient> patients;
    private Map<Patient, String> notes;

    @Override
    public void updatePassword(String newPassword) {
        // Logic to update password
    }

    public void addPatient(Patient patient) {
        // Logic to add a patient
    }

    public void removePatient(Patient patient) {
        // Logic to remove a patient
    }

    public void setInitialRom(Patient patient, int minRom, int maxRom) {
        // Logic to set initial ROM for a patient
    }

    public void addTherapistNote(Patient patient, String note) {
        // Logic to add a note for a patient
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

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
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

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public Map<Patient, String> getNotes() {
        return notes;
    }

    public void setNotes(Map<Patient, String> notes) {
        this.notes = notes;
    }
}
