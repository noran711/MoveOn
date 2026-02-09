package com.examplehjhk.moveon.auth;

import android.content.Context;
import android.database.Cursor;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

/**
 * AuthManager handles the business logic for user authentication,
 * including logging in and registering new accounts.
 */
public class AuthManager {

    /**
     * Inner class to encapsulate the result of an authentication operation.
     */
    public static class Result {
        public final boolean ok;       // Indicates if the operation was successful
        public final String message;   // Feedback message for the UI
        public final User user;        // The user object

        private Result(boolean ok, String message, User user) {
            this.ok = ok;
            this.message = message;
            this.user = user;
        }

        // Factory method for successful results
        public static Result success(String message, User user) {
            return new Result(true, message, user);
        }

        // Factory method for failed results
        public static Result fail(String message) {
            return new Result(false, message, null);
        }
    }

    private final DBHelper dbHelper;

    public AuthManager(Context context) {
        // Initialize the Database with the application context to avoid leaks
        this.dbHelper = new DBHelper(context.getApplicationContext());
    }

    /**
     * Validates credentials and retrieves user data from the database.
     */
    public Result login(String username, String password) {
        // Trim inputs to handle accidental whitespace
        String user = username == null ? "" : username.trim();
        String pass = password == null ? "" : password.trim();

        // Basic validation for empty fields
        if (user.isEmpty() || pass.isEmpty()) {
            return Result.fail("Please enter both username and password.");
        }

        // Query the database for a matching user
        Cursor c = dbHelper.getUserForLogin(user, pass);

        if (c == null) {
            return Result.fail("Invalid login credentials.");
        }

        try {
            // Check if any record was found
            if (!c.moveToFirst()) {
                return Result.fail("Invalid login credentials.");
            }

            // Extract user data from the database cursor
            String fName  = c.getString(c.getColumnIndexOrThrow("first_name"));
            String lName  = c.getString(c.getColumnIndexOrThrow("last_name"));
            String uName  = c.getString(c.getColumnIndexOrThrow("username"));
            String role   = c.getString(c.getColumnIndexOrThrow("role"));
            String gender = c.getString(c.getColumnIndexOrThrow("gender"));

            // Construct the User domain object
            User loggedInUser = new User();
            loggedInUser.firstName = fName;
            loggedInUser.lastName  = lName;
            loggedInUser.username  = uName;
            loggedInUser.role      = role;
            loggedInUser.gender    = gender;

            return Result.success("Welcome, " + fName, loggedInUser);

        } finally {
            // close to prevent memory leaks
            c.close();
        }
    }

    /**
     * Registers a new user after validating inputs and checking for duplicates.
     */
    public Result register(String firstName, String lastName, String birthDate,
                           String phone, String username, String password, String confirm,
                           boolean isFemale, boolean isPatient) {

        // Sanitize all inputs
        String f = safe(firstName);
        String l = safe(lastName);
        String b = safe(birthDate);
        String p = safe(phone);
        String u = safe(username);
        String pw = safe(password);
        String cf = safe(confirm);

        // Ensure all required fields are provided
        if (f.isEmpty() || l.isEmpty() || b.isEmpty()
                || p.isEmpty() || u.isEmpty() || pw.isEmpty() || cf.isEmpty()) {
            return Result.fail("Please fill in all fields.");
        }

        // Check if passwords match
        if (!pw.equals(cf)) {
            return Result.fail("Passwords do not match.");
        }

        // Convert boolean flags to display strings
        String roleText   = isPatient ? "Patient" : "Therapist";
        String genderText = isFemale  ? "Female"  : "Male";

        // Attempt to insert the new user into the database
        boolean ok = dbHelper.insertUser(
                f, l, b, p,
                u, pw,
                genderText, roleText
        );

        if (!ok) {
            return Result.fail("Registration failed (username might already be taken).");
        }

        return Result.success(roleText + " (" + genderText + ") successfully registered.", null);
    }

    /**
     * Helper method to prevent NullPointerExceptions and trim strings.
     */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}