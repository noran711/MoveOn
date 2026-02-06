package com.examplehjhk.moveon.domain;

public class Patient extends User {
    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;
    public String gender; // Female/Male

    public Patient() {}

    public Patient(int id, String username, String password) {
        super(id, username, password);
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
}
