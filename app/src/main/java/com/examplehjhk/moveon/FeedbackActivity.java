package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.domain.User;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView statusText = findViewById(R.id.statusText);
        TextView levelDisplayText = findViewById(R.id.levelDisplayText);
        TextView finalScoreText = findViewById(R.id.finalScoreText);
        TextView finalRomText = findViewById(R.id.finalRomText);
        TextView finalSupportText = findViewById(R.id.finalSupportText);
        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // Receive data
        User currentUser = (User) getIntent().getSerializableExtra("user");
        int score = getIntent().getIntExtra("score", 0);
        String rom = getIntent().getStringExtra("rom");
        String support = getIntent().getStringExtra("support");
        int level = getIntent().getIntExtra("level", 1);
        boolean success = getIntent().getBooleanExtra("success", false);

        // Update UI
        if (success && score >= 30) {
            statusText.setText("Level Successful!");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnRestart.setText("Next Level");
        } else {
            statusText.setText("Game Over!");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnRestart.setText("Retry Level");
        }

        levelDisplayText.setText("Level " + level);
        finalScoreText.setText("Score: " + score + " / 30");
        finalRomText.setText("ROM: " + rom);
        finalSupportText.setText("Support: " + support);

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, GameActivity.class);
            intent.putExtra("user", currentUser);

            // If successful, go to next level, otherwise repeat same level
            if (success && score >= 30) {
                intent.putExtra("nextLevel", level + 1);
            } else {
                intent.putExtra("nextLevel", level);
            }
            startActivity(intent);
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
            intent.putExtra("user", currentUser);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
