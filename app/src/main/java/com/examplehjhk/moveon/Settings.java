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

import com.examplehjhk.moveon.data.DBHelper;      // ✅ DEIN DBHelper
import com.examplehjhk.moveon.domain.User;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {

    private TextView lblFullName;
    private TextView usernameValue;
    private TextView passwordValue;

    private RadioButton btnradioPatient;
    private RadioButton btnradioTherapist;
    private RadioButton btnradioMale;
    private RadioButton btnradioFemale;

    private TextView romValue;
    private TextView romIncreaseValue;
    private TextView supportValue;

    private SwitchMaterial switchDarkMode;
    private CheckBox chkNotifications;
    private SharedPreferences sharedPreferences;

    private User currentUser;
    private DBHelper dbHelper;

    // ✅ aktuelle Werte (kommen aus DB)
    private int currentRom = 30;
    private int currentRomIncrease = 5;
    private int currentSupportPercent = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        currentUser = (User) getIntent().getSerializableExtra("user");
        dbHelper = new DBHelper(this);

        // UI
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

        // ✅ 1) DB Settings laden/erzeugen (nur wenn Patient)
        loadOrCreatePatientSettings();

        // ✅ 2) UI befüllen
        loadSettingsUI();

        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener(null);
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Dialogs
        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());

        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.romIncreaseLayout).setOnClickListener(v -> showChangeRomIncreaseDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());

        // Save
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> {
                saveAllSettings();
                Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }

        // Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Home Button
        ImageView homeButton = findViewById(R.id.btnHome);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        setupBottomNavigation();
    }

    /**
     * ✅ DB lesen – wenn noch kein Eintrag existiert:
     *    -> defaults setzen und in DB schreiben
     */
    private void loadOrCreatePatientSettings() {
        if (currentUser == null) return;

        if (!"Patient".equalsIgnoreCase(currentUser.role)) {
            // Therapeut hat keine eigenen patient_settings in dieser Tabelle
            return;
        }

        Cursor c = null;
        try {
            c = dbHelper.getPatientSettings(currentUser.username);

            if (c != null && c.moveToFirst()) {
                currentRom = c.getInt(c.getColumnIndexOrThrow("rom"));
                currentRomIncrease = c.getInt(c.getColumnIndexOrThrow("rom_increase"));
                currentSupportPercent = c.getInt(c.getColumnIndexOrThrow("support_percent"));
            } else {
                // ✅ kein Datensatz vorhanden -> defaults anlegen
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

    private void loadSettingsUI() {
        if (currentUser != null) {
            lblFullName.setText(currentUser.firstName + " " + currentUser.lastName);
            usernameValue.setText(currentUser.username);

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

        // ✅ ROM-Werte aus DB anzeigen (nicht aus SharedPrefs)
        romValue.setText(currentRom + "°");
        romIncreaseValue.setText(currentRomIncrease + "°");
        supportValue.setText(currentSupportPercent + " %");

        passwordValue.setText("••••••••");

        if (chkNotifications != null) {
            chkNotifications.setChecked(sharedPreferences.getBoolean("allow_notifications", true));
        }
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        if (chkNotifications != null) editor.putBoolean("allow_notifications", chkNotifications.isChecked());

        // optional fallback Werte
        editor.putString("rom", currentRom + "°");
        editor.putString("rom_increase", currentRomIncrease + "°");
        editor.putString("support", currentSupportPercent + " %");
        editor.apply();

        // ✅ WICHTIG: wenn Patient -> DB updaten
        if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
            dbHelper.upsertPatientSettings(currentUser.username, currentRom, currentRomIncrease, currentSupportPercent);
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        ImageView iconSettings = findViewById(R.id.iconSettings);
        TextView textSettings = findViewById(R.id.textSettings);
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

    // ---------- Dialoge: schreiben direkt in current... + UI + DB ----------

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
                val = Math.max(0, Math.min(90, val));
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
                val = Math.max(0, Math.min(30, val));
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
                val = Math.max(0, Math.min(100, val));
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

    // ---------- helpers ----------
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
