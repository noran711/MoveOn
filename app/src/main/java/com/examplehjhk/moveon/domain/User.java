package com.examplehjhk.moveon.domain;

import java.io.Serializable;

public class User implements Serializable {
    public int id;

    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;

    public String username;
    public String password;

    public String gender;   // "Female" / "Male"
    public String role;     // "Patient" / "Therapeut"

    public String getFullName() {
        String fn = firstName == null ? "" : firstName;
        String ln = lastName == null ? "" : lastName;
        return (fn + " " + ln).trim();
    }
}
