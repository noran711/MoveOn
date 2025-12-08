package com.examplehjhk.moveon;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class User {    @PrimaryKey(autoGenerate = true) // WICHTIG!
public int id;

    public String firstName;
    public String lastName;
    public String birthDate;
    public String phone;
    public String username;
    public String password;
    public String gender;   // "Female" / "Male"
    public String role;     // "Patient" / "Therapeut"
}
