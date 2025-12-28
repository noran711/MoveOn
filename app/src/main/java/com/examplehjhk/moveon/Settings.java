package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    private TextView lblFullName;
    private TextView usernameValue;
    private TextView passwordValue;
    private RadioButton btnradioPatient;
    private RadioButton btnradioTherapist;
    private TextView romValue;
    private TextView supportValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        lblFullName = findViewById(R.id.lblFullName);
        usernameValue = findViewById(R.id.usernameValue);
        passwordValue = findViewById(R.id.passwordValue);
        btnradioPatient = findViewById(R.id.btnradioPatient);
        btnradioTherapist = findViewById(R.id.btnradioTherapist);
        romValue = findViewById(R.id.romValue);
        supportValue = findViewById(R.id.supportValue);

        User user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            lblFullName.setText(fullName);
            usernameValue.setText(user.getUsername());
        }

        ImageView homeButton = findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.changeUsernameButton).setOnClickListener(v -> showChangeUsernameDialog());
        findViewById(R.id.changePasswordButton).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.romLayout).setOnClickListener(v -> showChangeRomDialog());
        findViewById(R.id.supportLayout).setOnClickListener(v -> showChangeSupportDialog());
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                usernameValue.setText(newUsername);
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newPassword = input.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                passwordValue.setText("••••••••");
                 // In a real app, you would save the hashed password
            } else {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void showChangeRomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change ROM");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newRom = input.getText().toString().trim();
            if (!newRom.isEmpty()) {
                romValue.setText(newRom + " grad");
            } else {
                Toast.makeText(this, "ROM value cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showChangeSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Support");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newSupport = input.getText().toString().trim();
            if (!newSupport.isEmpty()) {
                supportValue.setText(newSupport);
            } else {
                Toast.makeText(this, "Support value cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
