package com.examplehjhk.moveon;

import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for Therapists to view and manage specific patient details.
 * It allows the therapist to adjust therapy parameters (ROM, Support)
 * and view a history of the patient's game sessions.
 */
public class PatientDetailActivity extends AppCompatActivity {

    // Domain data
    private User therapist;
    private String patientUsername;

    // Database access
    private DBHelper db;

    // UI Components
    private TextView txtPatientTitle;
    private TextView txtSettings;
    private ListView listSessions;

    // Therapy parameters
    private int rom = 30;              // Base Range of Motion in degrees
    private int romIncrease = 5;       // ROM increase per level
    private int supportPercent = 10;   // assistance percentage

    // List for session history display
    private final ArrayList<String> sessionLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        // Hide the standard Action Bar for a custom UI look
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialize Data & Database
        // Retrieve passed data from the TherapistDashboardActivity
        therapist = (User) getIntent().getSerializableExtra("user");
        patientUsername = getIntent().getStringExtra("patient_username");

        db = new DBHelper(this);

        // UI Binding
        txtPatientTitle = findViewById(R.id.txtPatientTitle);
        txtSettings = findViewById(R.id.txtSettings);
        listSessions = findViewById(R.id.listSessions);

        Button btnEditRom = findViewById(R.id.btnEditRom);
        Button btnEditRomIncrease = findViewById(R.id.btnEditRomIncrease);
        Button btnEditSupport = findViewById(R.id.btnEditSupport);

        // Display the selected patient's name
        txtPatientTitle.setText("Patient: " + patientUsername);

        // Load & Render Data
        loadSettings();    // Get therapy parameters from DB
        renderSettings();  // Show parameters on UI
        loadSessions();    // Fetch and display game history

        // Button Click Listeners

        // Edit Base ROM
        btnEditRom.setOnClickListener(v -> editIntDialog("ROM (Degrees)", rom, val -> {
            rom = clamp(val, 0, 90); // ROM cannot exceed 90 degrees
            saveSettings();
        }));

        // Edit ROM Progression
        btnEditRomIncrease.setOnClickListener(v -> editIntDialog("ROM Increase (Degrees)", romIncrease, val -> {
            romIncrease = clamp(val, 0, 30);
            saveSettings();
        }));

        // Edit Support Percentage
        btnEditSupport.setOnClickListener(v -> editIntDialog("Support (%)", supportPercent, val -> {
            supportPercent = clamp(val, 0, 100);
            saveSettings();
        }));
    }

    /**
     * Retrieves specific therapy settings for the current patient from the SQLite database.
     */
    private void loadSettings() {
        Cursor c = db.getPatientSettings(patientUsername);
        if (c != null) {
            if (c.moveToFirst()) {
                rom = c.getInt(c.getColumnIndexOrThrow("rom"));
                romIncrease = c.getInt(c.getColumnIndexOrThrow("rom_increase"));
                supportPercent = c.getInt(c.getColumnIndexOrThrow("support_percent"));
            }
            c.close();
        }
    }

    /**
     * Persists adjusted therapy settings to the database and updates the UI.
     */
    private void saveSettings() {
        db.upsertPatientSettings(patientUsername, rom, romIncrease, supportPercent);
        renderSettings();
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the text display for current therapy parameters.
     */
    private void renderSettings() {
        txtSettings.setText("ROM: " + rom + "° | ROM+ per Level: " + romIncrease + "° | Support: " + supportPercent + "%");
    }

    /**
     * Loads the game history for this patient and populates the ListView.
     */
    private void loadSessions() {
        sessionLines.clear();

        Cursor c = db.getSessionsForPatient(patientUsername);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        if (c != null) {
            while (c.moveToNext()) {
                int level = c.getInt(c.getColumnIndexOrThrow("level_number"));
                int score = c.getInt(c.getColumnIndexOrThrow("obstacles_cleared"));
                boolean success = c.getInt(c.getColumnIndexOrThrow("success")) == 1;
                long start = c.getLong(c.getColumnIndexOrThrow("start_time_ms"));
                int dur = c.getInt(c.getColumnIndexOrThrow("duration_sec"));

                // Create a readable summary string for each session
                String line = sdf.format(new Date(start))
                        + " | Lvl " + level
                        + " | Score " + score
                        + " | " + (success ? "SUCCESS" : "FAIL")
                        + " | " + dur + "s";
                sessionLines.add(line);
            }
            c.close();
        }

        // Set the adapter to display the list in the UI
        listSessions.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessionLines));
    }

    // Functional interface for handling numeric inputs from dialogs
    private interface IntCallback { void onValue(int v); }

    /**
     * Displays a popup dialog to edit a numeric therapy value.
     *
     * @param title   The title of the dialog.
     * @param current The current value to be edited.
     * @param cb      Callback to handle the new value once 'OK' is clicked.
     */
    private void editIntDialog(String title, int current, IntCallback cb) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);

        // Create an input field limited to numbers
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(current));
        b.setView(input);

        b.setPositiveButton("OK", (d, w) -> {
            try {
                int v = Integer.parseInt(input.getText().toString().trim());
                cb.onValue(v);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancel", (d, w) -> d.dismiss());
        b.show();
    }

    /**
     * Ensures a value stays within the defined minimum and maximum bounds.
     */
    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}