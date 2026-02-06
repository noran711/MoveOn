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

public class PatientDetailActivity extends AppCompatActivity {

    private User therapist;
    private String patientUsername;

    private DBHelper db;

    private TextView txtPatientTitle;
    private TextView txtSettings;
    private ListView listSessions;

    private int rom = 30;
    private int romIncrease = 5;
    private int supportPercent = 10;

    private final ArrayList<String> sessionLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        therapist = (User) getIntent().getSerializableExtra("user");
        patientUsername = getIntent().getStringExtra("patient_username");

        db = new DBHelper(this);

        txtPatientTitle = findViewById(R.id.txtPatientTitle);
        txtSettings = findViewById(R.id.txtSettings);
        listSessions = findViewById(R.id.listSessions);

        Button btnEditRom = findViewById(R.id.btnEditRom);
        Button btnEditRomIncrease = findViewById(R.id.btnEditRomIncrease);
        Button btnEditSupport = findViewById(R.id.btnEditSupport);

        txtPatientTitle.setText("Patient: " + patientUsername);

        loadSettings();
        renderSettings();
        loadSessions();

        btnEditRom.setOnClickListener(v -> editIntDialog("ROM (Grad)", rom, val -> {
            rom = clamp(val, 0, 90);
            saveSettings();
        }));

        btnEditRomIncrease.setOnClickListener(v -> editIntDialog("ROM Increase (Grad)", romIncrease, val -> {
            romIncrease = clamp(val, 0, 30);
            saveSettings();
        }));

        btnEditSupport.setOnClickListener(v -> editIntDialog("Support (%)", supportPercent, val -> {
            supportPercent = clamp(val, 0, 100);
            saveSettings();
        }));
    }

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

    private void saveSettings() {
        db.upsertPatientSettings(patientUsername, rom, romIncrease, supportPercent);
        renderSettings();
        Toast.makeText(this, "Settings gespeichert", Toast.LENGTH_SHORT).show();
    }

    private void renderSettings() {
        txtSettings.setText("ROM: " + rom + "° | ROM+ pro Level: " + romIncrease + "° | Support: " + supportPercent + "%");
    }

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

                String line = sdf.format(new Date(start))
                        + " | Level " + level
                        + " | Score " + score
                        + " | " + (success ? "SUCCESS" : "FAIL")
                        + " | " + dur + "s";
                sessionLines.add(line);
            }
            c.close();
        }

        listSessions.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessionLines));
    }

    private interface IntCallback { void onValue(int v); }

    private void editIntDialog(String title, int current, IntCallback cb) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(current));
        b.setView(input);

        b.setPositiveButton("OK", (d, w) -> {
            try {
                int v = Integer.parseInt(input.getText().toString().trim());
                cb.onValue(v);
            } catch (Exception e) {
                Toast.makeText(this, "Ungültige Zahl", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Abbrechen", (d, w) -> d.dismiss());
        b.show();
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
