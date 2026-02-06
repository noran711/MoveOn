package com.examplehjhk.moveon.domain;

public class Patient extends User {

    public Patient() {
        super(); // ruft User()
        this.role = "Patient";
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
