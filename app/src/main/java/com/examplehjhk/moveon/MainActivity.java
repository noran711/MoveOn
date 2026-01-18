package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private User currentUser;
    private TextView streakCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = (User) getIntent().getSerializableExtra("user");

        // Dark Mode laden
        SharedPreferences settingsPrefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Streak UI initialisieren
        streakCountText = findViewById(R.id.streakCountText);
        checkAndRefreshStreak();

        // Level Buttons (Level 1)
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> {
            updateStreakOnPlay();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        // Settings Button
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
    }

    /**
     * Prüft beim Öffnen der App, ob der Streak gerissen ist.
     */
    private void checkAndRefreshStreak() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        if (lastPlayedMillis > 0) {
            Calendar today = getStartOfDay(Calendar.getInstance());
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            // Differenz in Tagen berechnen
            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff > 1) {
                // Länger als einen Tag Pause gemacht -> Streak zurücksetzen
                currentStreak = 0;
                prefs.edit().putInt("streak_count", 0).apply();
            }
        }
        streakCountText.setText(String.valueOf(currentStreak));
    }

    /**
     * Wird aufgerufen, wenn ein Spiel gestartet wird.
     */
    private void updateStreakOnPlay() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        Calendar today = getStartOfDay(Calendar.getInstance());
        
        if (lastPlayedMillis == 0) {
            // Allererstes Mal spielen
            currentStreak = 1;
        } else {
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff == 1) {
                // Genau ein Tag später -> Streak +1
                currentStreak++;
            } else if (daysDiff > 1) {
                // Streak war gerissen -> Neustart bei 1
                currentStreak = 1;
            }
            // Bei daysDiff == 0 (heute schon gespielt) bleibt der Streak gleich
        }

        prefs.edit()
            .putInt("streak_count", currentStreak)
            .putLong("last_played_date", System.currentTimeMillis())
            .apply();
        
        streakCountText.setText(String.valueOf(currentStreak));
    }

    private Calendar getStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
