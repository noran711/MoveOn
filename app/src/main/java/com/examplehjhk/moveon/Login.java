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

public class Login extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private RadioGroup radioGroupRole;
    private RadioButton radioPatient, radioTherapist;
    private Button buttonLogin;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ⬇️ wichtig: hier dein Login-Layout verwenden, NICHT start_screen
        setContentView(R.layout.activity_login);

        // Views aus dem Layout holen
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        radioPatient = findViewById(R.id.radioPatient);
        radioTherapist = findViewById(R.id.radioTherapist);
        buttonLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);

        // Login-Button
        buttonLogin.setOnClickListener(v -> {
            String user = editUsername.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(Login.this,
                        "Bitte Benutzername und Passwort eingeben",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isTherapist = radioTherapist.isChecked();

            // TODO: hier später echte Prüfung (Datenbank/Firebase)

            // Weiter zum Home-Screen (MainActivity)
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("isTherapist", isTherapist);
            startActivity(intent);
            finish();
        });

        // Registrieren-Text (Platzhalter)
        textRegister.setOnClickListener(v ->
                Toast.makeText(Login.this,
                        "Registrierung kommt später 😊",
                        Toast.LENGTH_SHORT).show()
        );
    }
}
