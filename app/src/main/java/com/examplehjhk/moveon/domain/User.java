package com.examplehjhk.moveon.domain;

import java.io.Serializable;

public class User implements Serializable {
    public int id;

    // Login / UML
    public String username;
    public String password;

    // Daten, die deine App aktuell benutzt
    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;
    public String gender;   // "Female" / "Male"
    public String role;     // "Patient" / "Therapeut"

    public User() {}

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
}
