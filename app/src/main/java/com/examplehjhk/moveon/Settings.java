package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.examplehjhk.moveon.data.DBHelper;
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

    // ✅ NEU
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        currentUser = (User) getIntent().getSerializableExtra("user");

        // ✅ NEU
        dbHelper = new DBHelper(this);

        // UI-Elemente initialisieren
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

        // 1) Laden der Einstellungen (Listener vorher entfernen)
        switchDarkMode.setOnCheckedChangeListener(null);
        loadSettings();

        // 2) Dark Mode Switch Listener
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 3) Klick-Listener für Dialoge
        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.romIncreaseLayout).setOnClickListener(v -> showChangeRomIncreaseDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());

        // 4) Save-Button
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> {
                saveAllSettings();
                Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }

        // 5) Logout Button
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
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.putExtra("user", currentUser);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // ✅ Wichtig: wenn Patient Settings öffnet, sollen neue DB-Werte sofort erscheinen
    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    private void loadSettings() {

        // ===== User Bereich =====
        if (currentUser != null) {
            // Name/Username aus dem User Objekt
            lblFullName.setText((currentUser.firstName == null ? "" : currentUser.firstName) + " "
                    + (currentUser.lastName == null ? "" : currentUser.lastName));
            usernameValue.setText(currentUser.username == null ? "" : currentUser.username);

            // Gender anzeigen (falls vorhanden)
            if ("Male".equalsIgnoreCase(currentUser.gender)) {
                if (btnradioMale != null) btnradioMale.setChecked(true);
            } else if ("Female".equalsIgnoreCase(currentUser.gender)) {
                if (btnradioFemale != null) btnradioFemale.setChecked(true);
            }

            // Rolle anzeigen
            if ("Patient".equalsIgnoreCase(currentUser.role)) {
                if (btnradioPatient != null) btnradioPatient.setChecked(true);
            } else if ("Therapeut".equalsIgnoreCase(currentUser.role)) {
                if (btnradioTherapist != null) btnradioTherapist.setChecked(true);
            }
        } else {
            // Fallback
            lblFullName.setText(sharedPreferences.getString("full_name", "Max Mustermann"));
            usernameValue.setText(sharedPreferences.getString("username", "exampleuser"));
        }

        // ===== Default aus SharedPrefs =====
        String romStr = sharedPreferences.getString("rom", "30°");
        String romIncStr = sharedPreferences.getString("rom_increase", "5°");
        String supportStr = sharedPreferences.getString("support", "10 %");

        // ===== DB Override für Patient (Therapeut hat evtl. Werte gesetzt) =====
        if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
            Cursor c = dbHelper.getPatientSettings(currentUser.username);
            if (c != null && c.moveToFirst()) {
                int rom = c.getInt(c.getColumnIndexOrThrow("rom"));
                int romInc = c.getInt(c.getColumnIndexOrThrow("rom_increase"));
                int support = c.getInt(c.getColumnIndexOrThrow("support_percent"));

                romStr = rom + "°";
                romIncStr = romInc + "°";
                supportStr = support + " %";
            }
            if (c != null) c.close();
        }

        // ===== UI setzen =====
        romValue.setText(romStr);
        romIncreaseValue.setText(romIncStr);
        supportValue.setText(supportStr);

        // ===== Rest wie gehabt =====
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        if (chkNotifications != null) {
            chkNotifications.setChecked(sharedPreferences.getBoolean("allow_notifications", true));
        }
        passwordValue.setText("••••••••");
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Dark Mode + Notifications bleiben in SharedPrefs
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        if (chkNotifications != null) {
            editor.putBoolean("allow_notifications", chkNotifications.isChecked());
        }

        // Username + Fullname in SharedPrefs (deine alte Logik)
        editor.putString("username", usernameValue.getText().toString());
        editor.putString("full_name", lblFullName.getText().toString());

        // ROM / ROM+ / Support speichern wir weiterhin auch in SharedPrefs als Fallback
        editor.putString("rom", romValue.getText().toString());
        editor.putString("rom_increase", romIncreaseValue.getText().toString());
        editor.putString("support", supportValue.getText().toString());

        if (btnradioPatient != null) editor.putBoolean("is_patient", btnradioPatient.isChecked());
        if (btnradioMale != null) editor.putBoolean("is_male", btnradioMale.isChecked());

        editor.apply();

        // ✅ NEU: Wenn Patient -> auch in DB speichern (damit Spiel + Settings gleich sind)
        if (currentUser != null && "Patient".equalsIgnoreCase(currentUser.role)) {
            int rom = parseIntSafe(romValue.getText().toString(), 30);
            int romInc = parseIntSafe(romIncreaseValue.getText().toString(), 5);
            int support = parseIntSafe(supportValue.getText().toString(), 10);

            // clamp sinnvoll
            rom = clamp(rom, 0, 90);
            romInc = clamp(romInc, 0, 30);
            support = clamp(support, 0, 100);

            dbHelper.upsertPatientSettings(currentUser.username, rom, romInc, support);
        }
    }

    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s.replace("°", "").replace("%", "").trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    // ===== Dialoge (wie bei dir) =====

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

    private void showChangeRomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Initial ROM");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) ->
                romValue.setText(input.getText().toString().trim() + "°"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeRomIncreaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ROM Increase per Level");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) ->
                romIncreaseValue.setText(input.getText().toString().trim() + "°"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Support");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) ->
                supportValue.setText(input.getText().toString().trim() + " %"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) ->
                passwordValue.setText("••••••••"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
