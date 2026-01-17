package com.examplehjhk.moveon;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true) // WICHTIG!
    public int id;

    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;
    public String username;
    public String password;
    public String gender;   // "Female" / "Male"
    public String role;     // "Patient" / "Therapeut"

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }
}
