package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Registrierung extends AppCompatActivity {

    private TextInputEditText editFirstName, editLastName, editBirthDate,
            editPhone, editUsernameRegister, editPasswordRegister, editPasswordConfirm;

    private TextInputLayout inputLayoutBirthDate;
    private MaterialButtonToggleGroup toggleGender, toggleRole;
    private MaterialButton buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Inputs
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editBirthDate = findViewById(R.id.editBirthDate);
        editPhone = findViewById(R.id.editPhone);
        editUsernameRegister = findViewById(R.id.editUsernameRegister);
        editPasswordRegister = findViewById(R.id.editPasswordRegister);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);

        // Layouts
        inputLayoutBirthDate = findViewById(R.id.inputLayoutBirthDate);

        // Toggles
        toggleGender = findViewById(R.id.toggleGender);
        toggleRole = findViewById(R.id.toggleRole);

        buttonRegister = findViewById(R.id.buttonRegister);

        // DatePicker öffnen beim Klicken aufs Feld
        editBirthDate.setOnClickListener(v -> showDatePicker());
        // und beim Klicken auf das Kalender-Icon
        inputLayoutBirthDate.setEndIconOnClickListener(v -> showDatePicker());

        // Register-Button
        buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = getText(editFirstName);
        String lastName = getText(editLastName);
        String birthDate = getText(editBirthDate);
        String phone = getText(editPhone);
        String username = getText(editUsernameRegister);
        String password = getText(editPasswordRegister);
        String confirm = getText(editPasswordConfirm);

        // Pflichtfelder prüfen
        if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()
                || phone.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {

            Toast.makeText(this, "Bitte alle Felder ausfüllen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Passwort prüfen
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwörter stimmen nicht überein.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isFemale = toggleGender.getCheckedButtonId() == R.id.btnFemale;
        boolean isPatient = toggleRole.getCheckedButtonId() == R.id.btnPatient;

        String roleText = isPatient ? "Patient" : "Therapeut";
        String genderText = isFemale ? "Female" : "Male";

        Toast.makeText(this,
                roleText + " (" + genderText + ") erfolgreich registriert.",
                Toast.LENGTH_SHORT).show();

        // zurück zum Login
        startActivity(new Intent(Registrierung.this, Login.class));
        finish();
    }

    // Text aus Eingabefeld lesen (mit Null-Schutz)
    private String getText(TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    // Material DatePicker für Geburtsdatum
    private void showDatePicker() {
        // Untere Grenze: 01.01.1900
        Calendar startCal = Calendar.getInstance();
        startCal.set(1900, Calendar.JANUARY, 1);
        long start = startCal.getTimeInMillis();

        // Obere Grenze: heute
        Calendar endCal = Calendar.getInstance();
        long end = endCal.getTimeInMillis();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(start);
        constraintsBuilder.setEnd(end);
        // Nur Daten bis heute erlauben
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        // Standard-Auswahl: z.B. Jahr 2000 (oder heute, wenn du willst)
        Calendar defaultCal = Calendar.getInstance();
        defaultCal.set(2000, Calendar.JANUARY, 1);
        long defaultSelection = defaultCal.getTimeInMillis();

        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Geburtsdatum wählen")
                        .setSelection(defaultSelection)
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selection);

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

            editBirthDate.setText(sdf.format(cal.getTime()));
        });

        datePicker.show(getSupportFragmentManager(), "BIRTHDATE_PICKER");
    }
}
