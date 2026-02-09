package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.domain.User;

/**
 * This activity displays feedback to the user after a game session ends.
 * It shows whether the level was passed or failed, the final score, and provides
 * options to either restart/continue or return to the main menu.
 */
public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Hide the default action bar for a cleaner, full-screen look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Link UI elements from the XML layout
        TextView statusText = findViewById(R.id.statusText);
        TextView levelDisplayText = findViewById(R.id.levelDisplayText);
        TextView finalScoreText = findViewById(R.id.finalScoreText);
        TextView finalRomText = findViewById(R.id.finalRomText);
        TextView finalSupportText = findViewById(R.id.finalSupportText);
        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // Receive data passed from GameActivity via Intent
        User currentUser = (User) getIntent().getSerializableExtra("user");
        int score = getIntent().getIntExtra("score", 0);
        String rom = getIntent().getStringExtra("rom");
        String support = getIntent().getStringExtra("support");
        int level = getIntent().getIntExtra("level", 1);
        boolean success = getIntent().getBooleanExtra("success", false);

        // Update the UI with the received data
        // Check if the level was successfully completed (won)
        if (success && score >= 30) {
            statusText.setText("Level Successful!");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnRestart.setText("Next Level"); // Change button text for winning state
        } else {
            // The level was failed
            statusText.setText("Game Over!");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnRestart.setText("Retry Level"); // Change button text for losing state
        }

        // Display the performance stats
        levelDisplayText.setText("Level " + level);
        finalScoreText.setText("Score: " + score + " / 30");
        finalRomText.setText("ROM: " + rom);
        finalSupportText.setText("Support: " + support);


        // Listener for the "Next Level" or "Retry Level" button
        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, GameActivity.class);
            // Pass the user session forward to the next game
            intent.putExtra("user", currentUser);

            // If the level was won, load the next level. Otherwise, reload the current one.
            if (success && score >= 30) {
                intent.putExtra("nextLevel", level + 1);
            } else {
                intent.putExtra("nextLevel", level);
            }
            startActivity(intent);
            finish(); // Close this feedback screen
        });

        // Listener for the "Back to Home" button
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
            // Pass the user session back to the main menu to stay logged in
            intent.putExtra("user", currentUser);
            // Flags to clear the activity stack and bring the existing MainActivity to the front
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close this feedback screen
        });
    }
}
