package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currentUser = (User) getIntent().getSerializableExtra("user");


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
        
        // 1. Laden der Einstellungen (Listener vorher entfernen)
        switchDarkMode.setOnCheckedChangeListener(null);
        loadSettings();

        // 2. Dark Mode Switch Listener
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 3. Klick-Listener für die Dialoge
        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.romIncreaseLayout).setOnClickListener(v -> showChangeRomIncreaseDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());

        // 4. Save-Button Logik
        if (buttonSave != null) {
            buttonSave.setOnClickListener(v -> {
                saveAllSettings();
                Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }

        // 5. Logout Button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Home Button (Logo)
        ImageView homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.putExtra("user", currentUser);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        // Highlight Settings (Current Screen)
        ImageView iconSettings = findViewById(R.id.iconSettings);
        TextView textSettings = findViewById(R.id.textSettings);
        iconSettings.setColorFilter(Color.parseColor("#048CFA"));
        textSettings.setTextColor(Color.parseColor("#048CFA"));

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, GroupsActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
    }

    private void loadSettings() {

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
        } else {
            lblFullName.setText(sharedPreferences.getString("full_name", "Max Mustermann"));
            usernameValue.setText(sharedPreferences.getString("username", "exampleuser"));
            if (btnradioMale != null) btnradioMale.setChecked(sharedPreferences.getBoolean("is_male", false));
            if (btnradioPatient != null) btnradioPatient.setChecked(sharedPreferences.getBoolean("is_patient", true));
        }

        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        if (chkNotifications != null) {
            chkNotifications.setChecked(sharedPreferences.getBoolean("allow_notifications", true));
        }
        romValue.setText(sharedPreferences.getString("rom", "30°"));
        romIncreaseValue.setText(sharedPreferences.getString("rom_increase", "5°"));
        supportValue.setText(sharedPreferences.getString("support", "10 %"));
        passwordValue.setText("••••••••");
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        if (chkNotifications != null) {
            editor.putBoolean("allow_notifications", chkNotifications.isChecked());
        }
        editor.putString("username", usernameValue.getText().toString());
        editor.putString("full_name", lblFullName.getText().toString());
        editor.putString("rom", romValue.getText().toString());
        editor.putString("rom_increase", romIncreaseValue.getText().toString());
        editor.putString("support", supportValue.getText().toString());
        if (btnradioPatient != null) editor.putBoolean("is_patient", btnradioPatient.isChecked());
        if (btnradioMale != null) editor.putBoolean("is_male", btnradioMale.isChecked());
        editor.apply();
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> usernameValue.setText(input.getText().toString().trim()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeRomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Initial ROM");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> romValue.setText(input.getText().toString().trim() + "°"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeRomIncreaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ROM Increase per Level");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> romIncreaseValue.setText(input.getText().toString().trim() + "°"));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Support");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> supportValue.setText(input.getText().toString().trim() + " %"));
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
}
