package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class start_screen extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2000; // 2 Sekunden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        // Optional: ActionBar ausblenden
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(start_screen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Splash schließen, damit man nicht zurückspringen kann
        }, SPLASH_DURATION);
    }
}
