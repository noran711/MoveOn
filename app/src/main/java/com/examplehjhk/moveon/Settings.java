package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.examplehjhk.moveon.data.DBHelper;      // Your SQLite Database Helper
import com.examplehjhk.moveon.domain.User;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Activity for managing user preferences and therapy-specific settings.
 * It handles profile display, Dark Mode toggling, and specialized
 * ROM settings for Patients.
 */
public class Settings extends AppCompatActivity {

    // UI Components for user profile
    private TextView lblFullName;
    private TextView usernameValue;
    private TextView passwordValue;

    private RadioButton btnradioPatient;
    private RadioButton btnradioTherapist;
    private RadioButton btnradioMale;
    private RadioButton btnradioFemale;

    // UI Components for therapy settings
    private TextView romValue;
    private TextView romIncreaseValue;
    private TextView supportValue;

    private SwitchMaterial switchDarkMode;
    private CheckBox chkNotifications;
    private SharedPreferences sharedPreferences;

    private User currentUser;
    private DBHelper dbHelper;

    // Current therapy values (synchronized with the Database)
    private int currentRom = 30;
    private int currentRomIncrease = 5;
    private int currentSupportPercent = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Retrieve the logged-in user and initialize database access
        currentUser = (User) getIntent().getSerializableExtra("user");
        dbHelper = new DBHelper(this);

        // Bind UI elements from the layout
        lblFullName = findViewById(R.id.lblFullName);
        usernameValue = findViewById(R.id.usernameValue);
        passwordValue = findViewById(R.id.passwordValue);
        btnradioPatient = findViewById(R.id.btnradioPatient);
        btnradioTherapist = findViewById(R.id.btnradioTherapist);
        btnradioMale = findViewById(R.id.btnradioMale);
        btnradioFemale = findViewById(R.id.btnradioFemale);

        romValue = findViewById(R.id.romValue);
        romIncreaseValue = findViewById(R.id.romIncreaseValue);
        supportValue = findViewById(R.id.supportValue);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        chkNotifications = findViewById(R.id.chkNotifications);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button btnLogout = findViewById(R.id.btnDeleteAccount);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Load or create therapy settings if the user is a Patient
        loadOrCreatePatientSettings();

        // Populate UI fields with user data
        loadSettingsUI();

        // Dark Mode Logic
        switchDarkMode.setOnCheckedChangeListener(null); // Avoid triggering listener during setup
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference and apply theme immediately
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Setup Dialog Listeners for Profile Updates
        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());

        // Setup Dialog Listeners for Therapy Values
        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.romIncreaseLayout).setOnClickListener(v -> showChangeRomIncreaseDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());

        // Global Save Button
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> {
                saveAllSettings();
                Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }

        // Logout Button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Clear task stack and return to Login screen
                Intent intent = new Intent(Settings.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Home/Back Button Logic
        ImageView homeButton = findViewById(R.id.btnHome);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                // Ensure we return to the existing home screen instance
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        setupBottomNavigation();
    }

    /**
     * Reads therapy settings from DB. If no entry exists for this patient,
     * it creates one using default values from SharedPreferences.
     */
    private void loadOrCreatePatientSettings() {
        if (currentUser == null) return;

        // Therapists do not have individual patient settings records
        if (!"Patient".equalsIgnoreCase(currentUser.role)) {
            return;
        }

        Cursor c = null;
        try {
            c = dbHelper.getPatientSettings(currentUser.username);

            if (c != null && c.moveToFirst()) {
                // Load existing data from the database
                currentRom = c.getInt(c.getColumnIndexOrThrow("rom"));
                currentRomIncrease = c.getInt(c.getColumnIndexOrThrow("rom_increase"));
                currentSupportPercent = c.getInt(c.getColumnIndexOrThrow("support_percent"));
            } else {
                // No record found: initialize defaults from SharedPreferences and create DB entry
                currentRom = parseDeg(sharedPreferences.getString("rom", "30°"), 30);
                currentRomIncrease = parseDeg(sharedPreferences.getString("rom_increase", "5°"), 5);
                currentSupportPercent = parsePercent(sharedPreferences.getString("support", "10%"), 10);

                dbHelper.upsertPatientSettings(
                        currentUser.username,
                        currentRom,
                        currentRomIncrease,
                        currentSupportPercent
                );
            }
        } finally {
            if (c != null) c.close();
        }
    }

    /**
     * Updates all UI components with the current user's profile and therapy values.
     */
    private void loadSettingsUI() {
        if (currentUser != null) {
            lblFullName.setText(currentUser.firstName + " " + currentUser.lastName);
            usernameValue.setText(currentUser.username);

            // Set radio button states based on identity
            if ("Male".equalsIgnoreCase(currentUser.gender)) {
                if (btnradioMale != null) btnradioMale.setChecked(true);
            } else {
                if (btnradioFemale != null) btnradioFemale.setChecked(true);
            }

            if ("Patient".equalsIgnoreCase(currentUser.role)) {
                if (btnradioPatient != null) btnradioPatient.setChecked(true);
            } else if ("Therapeut".equalsIgnoreCase(currentUser.role)) {
                if (btnradioTherapist != null) btnradioTherapist.setChecked(true);
            }
        }

        // Display current therapy values retrieved from the Database
        romValue.setText(currentRom + "°");
        romIncreaseValue.setText(currentRomIncrease + "°");
        supportValue.setText(currentSupportPercent + " %");

        passwordValue.setText("••••••••");

        if (chkNotifications != null) {
            chkNotifications.setChecked(sharedPreferences.getBoolean("allow_notifications", true));
        }
    }

    /**
     * Persists shared preferences and updates the database for Patients.
     */
    private void saveAllSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        if (chkNotifications != null) editor.putBoolean("allow_notifications", chkNotifications.isChecked());

        // Update fallback values in SharedPreferences for legacy compatibility
        editor.putString("rom", currentRom + "°");
        editor.putString("rom_increase", currentRomIncrease + "°");
        editor.putString("support", currentSupportPercent + " %");
        editor.apply();

        // Sync values to SQLite if the user is a Patient
        if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
            dbHelper.upsertPatientSettings(currentUser.username, currentRom, currentRomIncrease, currentSupportPercent);
        }
    }

    /**
     * Configures the Bottom Navigation Bar visual state and listeners.
     */
    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        ImageView iconSettings = findViewById(R.id.iconSettings);
        TextView textSettings = findViewById(R.id.textSettings);
        // Highlight the current "Settings" tab
        if (iconSettings != null) iconSettings.setColorFilter(Color.parseColor("#048CFA"));
        if (textSettings != null) textSettings.setTextColor(Color.parseColor("#048CFA"));

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            });
        }

        if (navGroups != null) {
            navGroups.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, GroupsActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            });
        }
    }

    // Dialogs: Input values directly update variables + UI + DB

    private void showChangeRomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Initial ROM");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(String.valueOf(currentRom));
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String txt = input.getText().toString().trim();
            if (txt.isEmpty()) return;
            try {
                int val = Integer.parseInt(txt);
                val = Math.max(0, Math.min(90, val)); // Constraint: 0 to 90 degrees
                currentRom = val;
                romValue.setText(currentRom + "°");

                if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
                    dbHelper.upsertPatientSettings(currentUser.username, currentRom, currentRomIncrease, currentSupportPercent);
                }
            } catch (Exception ignored) {}
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeRomIncreaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ROM Increase per Level");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(String.valueOf(currentRomIncrease));
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String txt = input.getText().toString().trim();
            if (txt.isEmpty()) return;
            try {
                int val = Integer.parseInt(txt);
                val = Math.max(0, Math.min(30, val)); // Constraint: 0 to 30 degrees
                currentRomIncrease = val;
                romIncreaseValue.setText(currentRomIncrease + "°");

                if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
                    dbHelper.upsertPatientSettings(currentUser.username, currentRom, currentRomIncrease, currentSupportPercent);
                }
            } catch (Exception ignored) {}
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Support (%)");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(String.valueOf(currentSupportPercent));
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String txt = input.getText().toString().trim();
            if (txt.isEmpty()) return;
            try {
                int val = Integer.parseInt(txt);
                val = Math.max(0, Math.min(100, val)); // Constraint: 0 to 100 percent
                currentSupportPercent = val;
                supportValue.setText(currentSupportPercent + " %");

                if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
                    dbHelper.upsertPatientSettings(currentUser.username, currentRom, currentRomIncrease, currentSupportPercent);
                }
            } catch (Exception ignored) {}
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) ->
                usernameValue.setText(input.getText().toString().trim()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> passwordValue.setText("••••••••"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Helper methods for string parsing

    private int parseDeg(String s, int fallback) {
        try { return Integer.parseInt(s.replace("°", "").trim()); }
        catch (Exception e) { return fallback; }
    }

    private int parsePercent(String s, int fallback) {
        try {
            return Integer.parseInt(s.replace("%", "").replace(" ", "").trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}