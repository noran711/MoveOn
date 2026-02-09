package com.examplehjhk.moveon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

/**
 * Activity responsible for handling user authentication.
 * It validates credentials against the local SQLite database and
 * redirects users to either the Patient or Therapist view.
 */
public class Login extends AppCompatActivity {

    // UI Component declarations
    private EditText editUsername, editPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioPatient, radioTherapist;
    private Button buttonLogin;
    private TextView textRegister;

    // Database helper instance
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the action bar for a cleaner login UI
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialize the database helper
        dbHelper = new DBHelper(this);

        // Link Java variables to XML layout components
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioPatient   = findViewById(R.id.radioPatient);
        radioTherapist = findViewById(R.id.radioTherapist);

        buttonLogin  = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);

        // Handle login button click
        buttonLogin.setOnClickListener(v -> attemptLogin());

        // Handle registration text click: Navigate to Registration screen
        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registrierung.class);
            startActivity(intent);
        });
    }

    /**
     * Logic for processing login attempts.
     * Validates input, queries the database, and starts the appropriate dashboard.
     */
    private void attemptLogin() {
        // Retrieve and sanitize user input
        String user = editUsername.getText().toString().trim();
        String pass = editPassword.getText().toString().trim();

        // Basic validation: Ensure fields are not empty
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query the database for a matching username/password pair
        Cursor c = dbHelper.getUserForLogin(user, pass);

        // Check if a user was found
        if (c == null || !c.moveToFirst()) {
            Toast.makeText(this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
            if (c != null) c.close();
            return;
        }

        // Map database cursor data to a User domain object
        User loggedInUser = new User();
        loggedInUser.firstName = c.getString(c.getColumnIndexOrThrow("first_name"));
        loggedInUser.lastName  = c.getString(c.getColumnIndexOrThrow("last_name"));
        loggedInUser.username  = c.getString(c.getColumnIndexOrThrow("username"));
        loggedInUser.role      = c.getString(c.getColumnIndexOrThrow("role"));
        loggedInUser.gender    = c.getString(c.getColumnIndexOrThrow("gender"));
        loggedInUser.birthDate = c.getString(c.getColumnIndexOrThrow("birth_date"));
        loggedInUser.phone     = c.getString(c.getColumnIndexOrThrow("phone"));

        // Always close the cursor to free up memory
        c.close();

        Toast.makeText(this, "Welcome, " + loggedInUser.firstName, Toast.LENGTH_SHORT).show();

        // Determine navigation path based on user role (Therapist or Patient)
        Intent intent;
        if ("Therapeut".equalsIgnoreCase(loggedInUser.role)) {
            // Redirect to Therapist Dashboard
            intent = new Intent(Login.this, TherapistDashboardActivity.class);
        } else {
            // Redirect to Patient Home Screen (MainActivity)
            intent = new Intent(Login.this, MainActivity.class);
        }

        // Pass the logged-in user object to the next screen
        intent.putExtra("user", loggedInUser);
        startActivity(intent);

        // Close Login screen so the user cannot navigate back to it via the back button
        finish();
    }
}