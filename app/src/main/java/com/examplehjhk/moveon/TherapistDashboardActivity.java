package com.examplehjhk.moveon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

import java.util.ArrayList;

/**
 * Main dashboard for users with the "Therapist" role.
 * It displays a list of all registered patients and allows navigation to their details.
 */
public class TherapistDashboardActivity extends AppCompatActivity {

    private User currentUser;
    private DBHelper db;

    // Internal lists to map the display labels to actual database usernames
    private final ArrayList<String> patientUsernames = new ArrayList<>();
    private final ArrayList<String> patientLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_dashboard);

        // Hide the standard action bar for a cleaner UI
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Session Validation
        // Retrieve the logged-in user and verify they have the correct permissions
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null || !"Therapeut".equalsIgnoreCase(currentUser.role)) {
            // Redirect unauthorized users to the login screen
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Initialize database helper
        db = new DBHelper(this);

        // UI Component Binding
        ListView listPatients = findViewById(R.id.listPatients);
        Button btnLogout = findViewById(R.id.btnLogoutTherapist);

        // Load Data & Populate List
        loadPatients();

        // Simple adapter to display the patient names in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                patientLabels
        );
        listPatients.setAdapter(adapter);

        // Event Listeners

        // Navigate to the PatientDetailActivity when a list item is clicked
        listPatients.setOnItemClickListener((parent, view, position, id) -> {
            String username = patientUsernames.get(position);
            Intent i = new Intent(this, PatientDetailActivity.class);
            i.putExtra("user", currentUser); // Pass therapist session
            i.putExtra("patient_username", username); // Pass selected patient
            startActivity(i);
        });

        // Handle Logout: Clear activity stack and return to Login screen
        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    /**
     * Queries the database for all users registered as patients
     * and prepares the data for the ListView.
     */
    private void loadPatients() {
        patientUsernames.clear();
        patientLabels.clear();

        // Query the 'users' table specifically for the "Patient" role
        Cursor c = db.getAllPatients();
        if (c != null) {
            while (c.moveToNext()) {
                // Extract column data
                String u = c.getString(c.getColumnIndexOrThrow("username"));
                String fn = c.getString(c.getColumnIndexOrThrow("first_name"));
                String ln = c.getString(c.getColumnIndexOrThrow("last_name"));

                // Store raw username for logic and a formatted label for the UI
                patientUsernames.add(u);
                patientLabels.add((ln + " " + fn).trim() + "  (" + u + ")");
            }
            c.close(); // Always close cursors to prevent memory leaks
        }
    }
}