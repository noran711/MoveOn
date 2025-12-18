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

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DBHelper(this);

        // Inputs
        editFirstName        = findViewById(R.id.editFirstName);
        editLastName         = findViewById(R.id.editLastName);
        editBirthDate        = findViewById(R.id.editBirthDate);
        editPhone            = findViewById(R.id.editPhone);
        editUsernameRegister = findViewById(R.id.editUsernameRegister);
        editPasswordRegister = findViewById(R.id.editPasswordRegister);
        editPasswordConfirm  = findViewById(R.id.editPasswordConfirm);

        inputLayoutBirthDate = findViewById(R.id.inputLayoutBirthDate);

        toggleGender = findViewById(R.id.toggleGender);
        toggleRole   = findViewById(R.id.toggleRole);

        buttonRegister = findViewById(R.id.buttonRegister);

        // Kalender öffnen
        editBirthDate.setOnClickListener(v -> showDatePicker());
        inputLayoutBirthDate.setEndIconOnClickListener(v -> showDatePicker());

        buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = getValue(editFirstName);
        String lastName  = getValue(editLastName);
        String birthDate = getValue(editBirthDate);
        String phone     = getValue(editPhone);
        String username  = getValue(editUsernameRegister);
        String password  = getValue(editPasswordRegister);
        String confirm   = getValue(editPasswordConfirm);

        if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()
                || phone.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {

            Toast.makeText(this, "Bitte alle Felder ausfüllen.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwörter stimmen nicht überein.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isFemale   = toggleGender.getCheckedButtonId() == R.id.btnFemale;
        boolean isPatient  = toggleRole.getCheckedButtonId()   == R.id.btnPatient;

        String roleText   = isPatient ? "Patient" : "Therapeut";
        String genderText = isFemale  ? "Female"  : "Male";

        boolean ok = dbHelper.insertUser(
                firstName,
                lastName,
                birthDate,
                phone,
                username,
                password,
                genderText,
                roleText
        );

        if (!ok) {
            Toast.makeText(this,
                    "Fehler beim Speichern (evtl. Benutzername schon vergeben).",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this,
                roleText + " (" + genderText + ") erfolgreich registriert.",
                Toast.LENGTH_SHORT).show();

        startActivity(new Intent(Registrierung.this, Login.class));
        finish();
    }

    private String getValue(TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    private void showDatePicker() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(1900, Calendar.JANUARY, 1);
        long start = startCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        long end = endCal.getTimeInMillis();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(start)
                .setEnd(end)
                .setValidator(DateValidatorPointBackward.now());

        Calendar defaultCal = Calendar.getInstance();
        defaultCal.set(2000, Calendar.JANUARY, 1);

        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Geburtsdatum wählen")
                        .setSelection(defaultCal.getTimeInMillis())
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
