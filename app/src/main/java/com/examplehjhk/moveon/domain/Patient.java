package com.examplehjhk.moveon.domain;

/**
 * Represents a Patient in the system.
 * This class extends the base User class and specifically sets the role to "Patient".
 */
public class Patient extends User {

    /**
     * Default constructor.
     * Initializes a new Patient and sets the default role.
     */
    public Patient() {
        super();
        // Hardcode the role as "Patient" for this specific class type
        this.role = "Patient";
    }

    /**
     * Retrieves the patient's first name.
     * @return The first name string inherited from the User class.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieves the patient's last name.
     * @return The last name string inherited from the User class.
     */
    public String getLastName() {
        return lastName;
    }
}