package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.examplehjhk.moveon.domain.User;

import java.util.Calendar;

/**
 * MainActivity serves as the primary dashboard for the patient.
 * It handles session persistence, daily streak logic, and navigation to game/settings.
 */
public class MainActivity extends AppCompatActivity {

    private User currentUser;
    private TextView streakCountText;
    private ScrollView levelScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // session management
        // Retrieve the logged-in user object passed from the Login activity
        currentUser = (User) getIntent().getSerializableExtra("user");

        // "Gatekeeper" logic: If no user is present, redirect to the Login screen immediately
        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // theme configuration
        // Check user preferences to determine if Dark Mode should be enabled
        SharedPreferences settingsPrefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        // Enable edge-to-edge display support
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjust layout padding to account for system bars (status/navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI initialization
        streakCountText = findViewById(R.id.streakCountText);
        levelScrollView = findViewById(R.id.levelScrollView);

        // Update the visual streak display based on stored data
        checkAndRefreshStreak();

        // Start Game Button Logic
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> {
            // Update the daily activity counter
            updateStreakOnPlay();

            // Navigate to the game screen and pass current user/progress data
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("user", currentUser);
            intent.putExtra("nextLevel", 1);
            startActivity(intent);
        });

        setupBottomNavigation();
        scrollToCurrentLevel();
    }

    /**
     * Configures the click listeners and visual state for the bottom navigation bar.
     */
    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        // Highlight the Home icon to show it is the currently active screen
        ImageView iconHome = findViewById(R.id.iconHome);
        TextView textHome = findViewById(R.id.textHome);
        iconHome.setColorFilter(Color.parseColor("#048CFA"));
        textHome.setTextColor(Color.parseColor("#048CFA"));

        // Navigate to the Social/Groups screen
        navGroups.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupsActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        // Navigate to the User Settings screen
        navSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
    }

    /**
     * Automatically scrolls the level selection view to the top.
     */
    private void scrollToCurrentLevel() {
        levelScrollView.post(() -> levelScrollView.fullScroll(ScrollView.FOCUS_UP));
    }

    /**
     * Checks the time since the last game was played.
     * If the user missed more than one day, the streak is reset to zero.
     */
    private void checkAndRefreshStreak() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        if (lastPlayedMillis > 0) {
            Calendar today = getStartOfDay(Calendar.getInstance());
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            // Calculate the difference in days
            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            // Reset if more than 1 full day has passed since last activity
            if (daysDiff > 1) {
                currentStreak = 0;
                prefs.edit().putInt("streak_count", 0).apply();
            }
        }
        streakCountText.setText(String.valueOf(currentStreak));
    }

    /**
     * Increments the streak count if played on a consecutive day,
     * or resets it to 1 if a break occurred.
     */
    private void updateStreakOnPlay() {
        SharedPreferences prefs = getSharedPreferences("streaks", MODE_PRIVATE);
        int currentStreak = prefs.getInt("streak_count", 0);
        long lastPlayedMillis = prefs.getLong("last_played_date", 0);

        Calendar today = getStartOfDay(Calendar.getInstance());

        if (lastPlayedMillis == 0) {
            // First time ever playing
            currentStreak = 1;
        } else {
            Calendar lastPlayed = getStartOfDay(Calendar.getInstance());
            lastPlayed.setTimeInMillis(lastPlayedMillis);

            long diff = today.getTimeInMillis() - lastPlayed.getTimeInMillis();
            long daysDiff = diff / (24 * 60 * 60 * 1000);

            if (daysDiff == 1) {
                // Consecutive day: increment
                currentStreak++;
            } else if (daysDiff > 1) {
                // Break in streak: restart at 1
                currentStreak = 1;
            }
        }

        // Persist the new streak values
        prefs.edit()
                .putInt("streak_count", currentStreak)
                .putLong("last_played_date", System.currentTimeMillis())
                .apply();

        streakCountText.setText(String.valueOf(currentStreak));
    }

    /**
     * Helper method to normalize a calendar instance to midnight.
     */
    private Calendar getStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}