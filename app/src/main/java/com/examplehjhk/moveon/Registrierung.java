package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.auth.AuthManager;
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

/**
 * Activity responsible for user registration.
 * It uses Material Design components for input and handles the registration
 * logic via the AuthManager class.
 */
public class Registrierung extends AppCompatActivity {

    // UI Components for user data input
    private TextInputEditText editFirstName, editLastName, editBirthDate,
            editPhone, editUsernameRegister, editPasswordRegister, editPasswordConfirm;

    private TextInputLayout inputLayoutBirthDate;

    // Toggle groups for selecting gender (Male/Female) and role (Patient/Therapist)
    private MaterialButtonToggleGroup toggleGender, toggleRole;
    private MaterialButton buttonRegister;

    // Logic handler for authentication and database insertion
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Hide the action bar for a cleaner, modern look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the AuthManager to handle the registration business logic
        authManager = new AuthManager(this);

        // Link Java variables to the Material UI components in the XML layout
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

        // Open the Material Date Picker when clicking the date field or its icon
        editBirthDate.setOnClickListener(v -> showDatePicker());
        inputLayoutBirthDate.setEndIconOnClickListener(v -> showDatePicker());

        // Set up the click listener for the final registration step
        buttonRegister.setOnClickListener(v -> registerUser());
    }

    /**
     * Collects all user input and sends it to the AuthManager for processing.
     */
    private void registerUser() {
        // Extract text from InputEditText components
        String firstName = getValue(editFirstName);
        String lastName  = getValue(editLastName);
        String birthDate = getValue(editBirthDate);
        String phone     = getValue(editPhone);
        String username  = getValue(editUsernameRegister);
        String password  = getValue(editPasswordRegister);
        String confirm   = getValue(editPasswordConfirm);

        // Determine selections from the ToggleGroups
        boolean isFemale   = toggleGender.getCheckedButtonId() == R.id.btnFemale;
        boolean isPatient  = toggleRole.getCheckedButtonId()   == R.id.btnPatient;

        // Perform registration via AuthManager
        AuthManager.Result res = authManager.register(
                firstName,
                lastName,
                birthDate,
                phone,
                username,
                password,
                confirm,
                isFemale,
                isPatient
        );

        // Show feedback (Success or Error message) to the user
        Toast.makeText(this, res.message,
                res.ok ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

        // If registration failed, stay on the screen to let the user fix inputs
        if (!res.ok) return;

        // If successful, navigate back to the Login screen
        startActivity(new Intent(Registrierung.this, Login.class));
        finish();
    }

    /**
     * Safely retrieves the string value from an EditText.
     * @return Trimmed string or an empty string if null.
     */
    private String getValue(TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }

    /**
     * Configures and displays a Material Design Date Picker.
     * Includes constraints to prevent selecting future dates.
     */
    private void showDatePicker() {
        // Set up bounds (from year 1900 until today)
        Calendar startCal = Calendar.getInstance();
        startCal.set(1900, Calendar.JANUARY, 1);
        long start = startCal.getTimeInMillis();

        Calendar endCal = Calendar.getInstance();
        long end = endCal.getTimeInMillis();

        // Build constraints: Start at 1900 and disallow selecting dates in the future
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(start)
                .setEnd(end)
                .setValidator(DateValidatorPointBackward.now());

        // Set a default selection for better UX
        Calendar defaultCal = Calendar.getInstance();
        defaultCal.set(2000, Calendar.JANUARY, 1);

        // Build the Material Design Date Picker dialog
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date of Birth")
                        .setSelection(defaultCal.getTimeInMillis())
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build();

        // Handle the date selection when the user clicks 'OK'
        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selection);

            // Format the selected date for display (DD.MM.YYYY)
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

            editBirthDate.setText(sdf.format(cal.getTime()));
        });

        // Show the picker dialog
        datePicker.show(getSupportFragmentManager(), "BIRTHDATE_PICKER");
    }
}