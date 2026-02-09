package com.examplehjhk.moveon.domain;

import java.io.Serializable;

/**
 * Represents a generic User in the system.
 * Implements Serializable to allow passing user objects between Activities via Intents.
 */
public class User implements Serializable {

    // Unique identifier for the user
    public int id;

    // Personal Information
    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;

    // Login Credentials
    public String username;
    public String password;

    // Identity and Access Control
    public String gender;   // Expected values: "Female" / "Male"
    public String role;     // Expected values: "Patient" / "Therapeut"

    /**
     * Combines the first and last name into a single string.
     * Handles null values to prevent displaying "null" as a string.
     *
     * @return The full name of the user, trimmed of leading/trailing whitespace.
     */
    public String getFullName() {
        // Use empty string if firstName is null
        String fn = firstName == null ? "" : firstName;

        // Use empty string if lastName is null
        String ln = lastName == null ? "" : lastName;

        // Concatenate and trim in case one of the names is missing
        return (fn + " " + ln).trim();
    }
}