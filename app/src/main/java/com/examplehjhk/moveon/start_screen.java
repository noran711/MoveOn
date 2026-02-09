package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * A splash screen activity that is displayed when the app first launches.
 * It automatically transitions to the Login screen after a short delay.
 */
public class start_screen extends AppCompatActivity {

    // The duration the splash screen will be visible, in milliseconds
    private static final long SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        // Hide the action bar for a clean, full-screen appearance.
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Use a Handler to delay the transition to the next activity.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Create an intent to launch the Login activity.
            Intent intent = new Intent(start_screen.this, Login.class);
            startActivity(intent);

            // Close the splash screen so the user cannot navigate back to it.
            finish();
        }, SPLASH_DURATION);
    }
}
