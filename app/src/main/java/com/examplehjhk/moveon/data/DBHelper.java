package com.examplehjhk.moveon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moveon.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

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
        // Alle Spalten abfragen, damit der Login-Prozess die Daten ins User-Objekt packen kann
        return db.query(
                TABLE_USERS,
                null, // null fragt alle Spalten ab
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );
    }
}
