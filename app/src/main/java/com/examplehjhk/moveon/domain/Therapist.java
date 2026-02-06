package com.examplehjhk.moveon.domain;

public class Therapist extends User {
    public String firstName;
    public String lastName;
    public String phone;
    public String gender;

    public Therapist() {}

    public Therapist(int id, String username, String password) {
        super(id, username, password);
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
}
