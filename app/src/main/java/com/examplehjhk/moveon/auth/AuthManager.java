package com.examplehjhk.moveon.auth;

import android.content.Context;
import android.database.Cursor;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

public class AuthManager {

    public static class Result {
        public final boolean ok;
        public final String message;
        public final User user; // beim Login gesetzt, beim Register null

        private Result(boolean ok, String message, User user) {
            this.ok = ok;
            this.message = message;
            this.user = user;
        }

        public static Result success(String message, User user) {
            return new Result(true, message, user);
        }

        public static Result fail(String message) {
            return new Result(false, message, null);
        }
    }

    private final DBHelper dbHelper;

    public AuthManager(Context context) {
        // ApplicationContext ist safer gegen Leaks
        this.dbHelper = new DBHelper(context.getApplicationContext());
    }

    /**
     * Login-Logik 1:1 aus deiner Login Activity.
     * Rückgabe: Result.ok + User (wie bisher), Message für Toast.
     */
    public Result login(String username, String password) {
        String user = username == null ? "" : username.trim();
        String pass = password == null ? "" : password.trim();

        if (user.isEmpty() || pass.isEmpty()) {
            return Result.fail("Bitte Benutzername und Passwort eingeben");
        }

        Cursor c = dbHelper.getUserForLogin(user, pass);

        if (c == null) {
            return Result.fail("Ungültige Login-Daten");
        }

        try {
            if (!c.moveToFirst()) {
                return Result.fail("Ungültige Login-Daten");
            }

            // Alle Daten aus der DB holen (wie vorher)
            String fName  = c.getString(c.getColumnIndexOrThrow("first_name"));
            String lName  = c.getString(c.getColumnIndexOrThrow("last_name"));
            String uName  = c.getString(c.getColumnIndexOrThrow("username"));
            String role   = c.getString(c.getColumnIndexOrThrow("role"));
            String gender = c.getString(c.getColumnIndexOrThrow("gender"));

            // User-Objekt erstellen (wie vorher)
            User loggedInUser = new User();
            loggedInUser.firstName = fName;
            loggedInUser.lastName  = lName;
            loggedInUser.username  = uName;
            loggedInUser.role      = role;
            loggedInUser.gender    = gender;

            return Result.success("Willkommen, " + fName, loggedInUser);

        } finally {
            c.close();
        }
    }

    /**
     * Registrierung 1:1 aus deiner Registrierung Activity.
     * Rückgabe: Result.ok + Message für Toast.
     */
    public Result register(String firstName, String lastName, String birthDate,
                           String phone, String username, String password, String confirm,
                           boolean isFemale, boolean isPatient) {

        String f = safe(firstName);
        String l = safe(lastName);
        String b = safe(birthDate);
        String p = safe(phone);
        String u = safe(username);
        String pw = safe(password);
        String cf = safe(confirm);

        if (f.isEmpty() || l.isEmpty() || b.isEmpty()
                || p.isEmpty() || u.isEmpty() || pw.isEmpty() || cf.isEmpty()) {
            return Result.fail("Bitte alle Felder ausfüllen.");
        }

        if (!pw.equals(cf)) {
            return Result.fail("Passwörter stimmen nicht überein.");
        }

        String roleText   = isPatient ? "Patient" : "Therapeut";
        String genderText = isFemale  ? "Female"  : "Male";

        boolean ok = dbHelper.insertUser(
                f, l, b, p,
                u, pw,
                genderText, roleText
        );

        if (!ok) {
            return Result.fail("Fehler beim Speichern (evtl. Benutzername schon vergeben).");
        }

        return Result.success(roleText + " (" + genderText + ") erfolgreich registriert.", null);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
