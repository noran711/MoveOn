package com.examplehjhk.moveon.domain;

/**
 * Represents a Therapist in the system.
 * This class extends the base User class and specifically sets the role to "Therapeut".
 */
public class Therapist extends User {

    /**
     * Default constructor.
     * Initializes a new Therapist and sets the default role.
     */
    public Therapist() {
        // Call the constructor of the parent User class
        super();

        // Hardcode the role as "Therapeut" for this specific class type
        this.role = "Therapeut";
    }
}