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

public class Login extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioPatient, radioTherapist;
    private Button buttonLogin;
    private TextView textRegister;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DBHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioPatient   = findViewById(R.id.radioPatient);
        radioTherapist = findViewById(R.id.radioTherapist);

        buttonLogin  = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);

        buttonLogin.setOnClickListener(v -> attemptLogin());

        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registrierung.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String user = editUsername.getText().toString().trim();
        String pass = editPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Bitte Benutzername und Passwort eingeben", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor c = dbHelper.getUserForLogin(user, pass);

        if (c == null || !c.moveToFirst()) {
            Toast.makeText(this, "Ungültige Login-Daten", Toast.LENGTH_SHORT).show();
            if (c != null) c.close();
            return;
        }

        String firstName = c.getString(c.getColumnIndexOrThrow("first_name"));
        String role      = c.getString(c.getColumnIndexOrThrow("role"));
        boolean isTherapist = "Therapeut".equals(role);

        c.close();

        Toast.makeText(this,
                "Willkommen, " + firstName + " (" + role + ")",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("isTherapist", isTherapist);
        startActivity(intent);
        finish();
    }
}
