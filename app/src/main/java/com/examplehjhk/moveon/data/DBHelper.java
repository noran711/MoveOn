package com.examplehjhk.moveon.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moveon.db";
    // ✅ VERSION HOCHSETZEN, sonst werden neue Tabellen nicht erstellt
    private static final int DB_VERSION = 2;

    // ===== USERS =====
    public static final String TABLE_USERS = "users";

    // ===== GAME SESSIONS =====
    public static final String TABLE_SESSIONS = "game_sessions";

    // ===== PATIENT SETTINGS (ROM etc. pro Patient) =====
    public static final String TABLE_PATIENT_SETTINGS = "patient_settings";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // USERS
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "birth_date TEXT, " +
                "phone TEXT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "gender TEXT, " +
                "role TEXT" +
                ");";
        db.execSQL(createUsers);

        // GAME SESSIONS
        String createSessions = "CREATE TABLE " + TABLE_SESSIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_username TEXT, " +
                "level_number INTEGER, " +
                "obstacles_cleared INTEGER, " +
                "success INTEGER, " +
                "start_time_ms INTEGER, " +
                "end_time_ms INTEGER, " +
                "duration_sec INTEGER" +
                ");";
        db.execSQL(createSessions);

        // PATIENT SETTINGS (ROM/SUPPORT pro Patient)
        String createPatientSettings = "CREATE TABLE " + TABLE_PATIENT_SETTINGS + " (" +
                "patient_username TEXT PRIMARY KEY, " +
                "rom INTEGER, " +
                "rom_increase INTEGER, " +
                "support_percent INTEGER" +
                ");";
        db.execSQL(createPatientSettings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Für einfach: neu aufbauen (Achtung: löscht Daten). Für Abgabe oft ok.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ===================== USERS =====================

    public boolean insertUser(String firstName, String lastName, String birthDate,
                              String phone, String username, String password,
                              String gender, String role) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("birth_date", birthDate);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", password);
        values.put("gender", gender);
        values.put("role", role);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Cursor getUserForLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                null,
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );
    }

    /** ✅ Therapeut: alle Patienten (nur role = Patient) */
    public Cursor getAllPatients() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                null,
                "role = ?",
                new String[]{"Patient"},
                null, null,
                "last_name ASC"
        );
    }

    // ===================== PATIENT SETTINGS =====================

    /** Insert oder Update Settings pro Patient */
    public void upsertPatientSettings(String patientUsername, int rom, int romIncrease, int supportPercent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("patient_username", patientUsername);
        v.put("rom", rom);
        v.put("rom_increase", romIncrease);
        v.put("support_percent", supportPercent);

        // replace = Upsert
        db.insertWithOnConflict(TABLE_PATIENT_SETTINGS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /** Lädt Settings für Patient (kann null sein wenn noch nicht gesetzt) */
    public Cursor getPatientSettings(String patientUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_PATIENT_SETTINGS,
                null,
                "patient_username = ?",
                new String[]{patientUsername},
                null, null, null
        );
    }

    // ===================== GAME SESSIONS =====================

    public void insertGameSession(String patientUsername,
                                  int levelNumber,
                                  int obstaclesCleared,
                                  boolean success,
                                  long startTimeMs,
                                  long endTimeMs,
                                  int durationSec) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("patient_username", patientUsername);
        v.put("level_number", levelNumber);
        v.put("obstacles_cleared", obstaclesCleared);
        v.put("success", success ? 1 : 0);
        v.put("start_time_ms", startTimeMs);
        v.put("end_time_ms", endTimeMs);
        v.put("duration_sec", durationSec);

        db.insert(TABLE_SESSIONS, null, v);
    }

    /** ✅ Therapeut/Patient: Sessions eines Patienten */
    public Cursor getSessionsForPatient(String patientUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_SESSIONS,
                null,
                "patient_username = ?",
                new String[]{patientUsername},
                null, null,
                "start_time_ms DESC"
        );
    }
}
