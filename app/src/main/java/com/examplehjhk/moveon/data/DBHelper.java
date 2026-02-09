package com.examplehjhk.moveon.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * DBHelper manages the local SQLite database for the application.
 * It handles table creation, version management, and data access.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database metadata
    private static final String DB_NAME = "moveon.db";
    private static final int DB_VERSION = 2;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_SESSIONS = "game_sessions";
    public static final String TABLE_PATIENT_SETTINGS = "patient_settings";

    /**
     * Constructor for the Database Helper.
     * @param context The application context.
     */
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Defines the schema for all required tables.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Define SQL statement to create the Users table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "birth_date TEXT, " +
                "phone TEXT, " +
                "username TEXT UNIQUE, " + // Unique constraint to prevent duplicate usernames
                "password TEXT, " +
                "gender TEXT, " +
                "role TEXT" +
                ");";
        db.execSQL(createUsers);

        // Define SQL statement to create the Game Sessions table
        String createSessions = "CREATE TABLE " + TABLE_SESSIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patient_username TEXT, " +
                "level_number INTEGER, " +
                "obstacles_cleared INTEGER, " +
                "success INTEGER, " + // Stored as 0 or 1
                "start_time_ms INTEGER, " +
                "end_time_ms INTEGER, " +
                "duration_sec INTEGER" +
                ");";
        db.execSQL(createSessions);

        // Define SQL statement for therapy-related settings (ROM and support)
        String createPatientSettings = "CREATE TABLE " + TABLE_PATIENT_SETTINGS + " (" +
                "patient_username TEXT PRIMARY KEY, " +
                "rom INTEGER, " +
                "rom_increase INTEGER, " +
                "support_percent INTEGER" +
                ");";
        db.execSQL(createPatientSettings);
    }

    /**
     * Called when the database needs to be upgraded.
     * Currently drops all tables and recreates them from scratch.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Inserts a new user record into the database.
     * @return true if successful, false otherwise.
     */
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

    /**
     * Queries the database for a specific user based on login credentials.
     * @return A Cursor pointing to the result set.
     */
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

    /**
     * Retrieves all users registered with the 'Patient' role.
     * Sorted alphabetically by last name.
     */
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

    /**
     * Updates or inserts (Upsert) therapy settings for a specific patient.
     * Uses CONFLICT_REPLACE to overwrite existing entries for the same username.
     */
    public void upsertPatientSettings(String patientUsername, int rom, int romIncrease, int supportPercent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("patient_username", patientUsername);
        v.put("rom", rom);
        v.put("rom_increase", romIncrease);
        v.put("support_percent", supportPercent);

        db.insertWithOnConflict(TABLE_PATIENT_SETTINGS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Retrieves specific therapy settings for a given patient.
     */
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

    /**
     * Logs the completion of a game session.
     */
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
        v.put("success", success ? 1 : 0); // Convert boolean to SQLite compatible integer
        v.put("start_time_ms", startTimeMs);
        v.put("end_time_ms", endTimeMs);
        v.put("duration_sec", durationSec);

        db.insert(TABLE_SESSIONS, null, v);
    }

    /**
     * Retrieves all game history for a specific patient, most recent sessions first.
     */
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