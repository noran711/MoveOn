package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
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
    private ScrollView levelScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = (User) getIntent().getSerializableExtra("user");

        // Optional: falls MainActivity mal ohne User gestartet wird
        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Dark Mode laden
        SharedPreferences settingsPrefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI-Elemente
        streakCountText = findViewById(R.id.streakCountText);
        levelScrollView = findViewById(R.id.levelScrollView);
        checkAndRefreshStreak();

        // Level 1 Button
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> {
            updateStreakOnPlay();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("user", currentUser);
            intent.putExtra("nextLevel", 1);
            startActivity(intent);
        });

        // Settings Button
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        // Automatisches Scrollen zum aktuellen Level (Beispiel Level 1)
        scrollToCurrentLevel();
    }

    private void scrollToCurrentLevel() {
        levelScrollView.post(() -> levelScrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    private void checkAndRefreshStreak() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        if (lastPlayedMillis > 0) {
            Calendar today = getStartOfDay(Calendar.getInstance());
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff > 1) {
                currentStreak = 0;
                prefs.edit().putInt("streak_count", 0).apply();
            }
        }
        streakCountText.setText(String.valueOf(currentStreak));
    }

    private void updateStreakOnPlay() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        Calendar today = getStartOfDay(Calendar.getInstance());

        if (lastPlayedMillis == 0) {
            currentStreak = 1;
        } else {
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff == 1) {
                currentStreak++;
            } else if (daysDiff > 1) {
                currentStreak = 1;
            }
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
