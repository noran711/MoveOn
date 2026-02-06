package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.auth.AuthManager;

public class Login extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioPatient, radioTherapist;
    private Button buttonLogin;
    private TextView textRegister;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authManager = new AuthManager(this);

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
        String user = editUsername.getText() == null ? "" : editUsername.getText().toString();
        String pass = editPassword.getText() == null ? "" : editPassword.getText().toString();

        AuthManager.Result res = authManager.login(user, pass);

        Toast.makeText(this, res.message, Toast.LENGTH_SHORT).show();

        if (!res.ok) return;

        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("user", res.user); // bleibt Serializable wie vorher
        startActivity(intent);
        finish();
    }
}
