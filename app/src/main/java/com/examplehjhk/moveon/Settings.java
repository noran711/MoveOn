package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private TextView supportValue;
    private SwitchMaterial switchDarkMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // UI-Elemente initialisieren
        lblFullName = findViewById(R.id.lblFullName);
        usernameValue = findViewById(R.id.usernameValue);
        passwordValue = findViewById(R.id.passwordValue);
        btnradioPatient = findViewById(R.id.btnradioPatient);
        btnradioTherapist = findViewById(R.id.btnradioTherapist);
        btnradioMale = findViewById(R.id.btnradioMale);
        btnradioFemale = findViewById(R.id.btnradioFemale);
        romValue = findViewById(R.id.romValue);
        supportValue = findViewById(R.id.supportValue);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        Button buttonSave = findViewById(R.id.buttonSave);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        
        // 1. Gespeicherte Daten oder User-Daten laden
        loadSettings();

        // Dark Mode Switch (sofortige Reaktion)
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 2. Klick-Listener für die Dialoge
        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());

        // 3. Save-Button Logik
        buttonSave.setOnClickListener(v -> {
            saveAllSettings();
            Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
        });

        // Home Button
        ImageView homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadSettings() {
        // Zuerst versuchen, Daten vom Intent (Login/Register) zu bekommen
        User user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            // Wenn User-Daten übergeben wurden, diese bevorzugen
            lblFullName.setText(user.firstName + " " + user.lastName);
            usernameValue.setText(user.username);
            
            // Gender setzen
            if ("Male".equalsIgnoreCase(user.gender)) {
                btnradioMale.setChecked(true);
            } else {
                btnradioFemale.setChecked(true);
            }
            
            // Rolle setzen
            if ("Patient".equalsIgnoreCase(user.role)) {
                btnradioPatient.setChecked(true);
            } else if ("Therapeut".equalsIgnoreCase(user.role)) {
                btnradioTherapist.setChecked(true);
            }
            
            // Direkt beim ersten Mal in SharedPreferences speichern
            saveAllSettings();
        } else {
            // Ansonsten aus SharedPreferences laden
            lblFullName.setText(sharedPreferences.getString("full_name", "Max Mustermann"));
            usernameValue.setText(sharedPreferences.getString("username", "exampleuser"));
            
            boolean isMale = sharedPreferences.getBoolean("is_male", false);
            btnradioMale.setChecked(isMale);
            btnradioFemale.setChecked(!isMale);

            boolean isPatient = sharedPreferences.getBoolean("is_patient", true);
            btnradioPatient.setChecked(isPatient);
            btnradioTherapist.setChecked(!isPatient);
        }

        // Andere Settings laden
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        romValue.setText(sharedPreferences.getString("rom", "30°"));
        supportValue.setText(sharedPreferences.getString("support", "10 %"));
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        editor.putString("username", usernameValue.getText().toString());
        editor.putString("full_name", lblFullName.getText().toString());
        editor.putString("rom", romValue.getText().toString());
        editor.putString("support", supportValue.getText().toString());
        editor.putBoolean("is_patient", btnradioPatient.isChecked());
        editor.putBoolean("is_male", btnradioMale.isChecked());
        
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
        builder.setTitle("Change ROM");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> romValue.setText(input.getText().toString().trim() + "°"));
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
