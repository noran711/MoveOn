package com.examplehjhk.moveon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private Button btnStart;
    private TextView scoreText;
    private TextView romInfoText;
    private TextView supportInfoText;

    private TextView levelInfoText;
    
    private int currentScore = 0;
    private int currentLevel = 1;
    private int currentROMValue = 90;
    private int romIncreaseValue = 5;
    private String supportString = "10%";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);
        scoreText = findViewById(R.id.scoreText);
        romInfoText = findViewById(R.id.romInfoText);
        supportInfoText = findViewById(R.id.supportInfoText);
        levelInfoText = findViewById(R.id.levelInfoText);

        // Load settings
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String romString = prefs.getString("rom", "30°");
        String increaseString = prefs.getString("rom_increase", "5°");
        supportString = prefs.getString("support", "10%");
        

        // Initial Level and values from intent or prefs
        currentLevel = getIntent().getIntExtra("nextLevel", 1);


        try {
            currentROMValue = Integer.parseInt(romString.replace("°", "").trim());
            romIncreaseValue = Integer.parseInt(increaseString.replace("°", "").trim());
        } catch (Exception e) {
            currentROMValue = 30;
            romIncreaseValue = 5;
        }


        // Apply ROM increase based on level
        if (currentLevel > 1) {
            currentROMValue += (currentLevel - 1) * romIncreaseValue;
            if (currentROMValue > 90) currentROMValue = 90; // Cap at 90
        }

        // Update UI
        romInfoText.setText("ROM: " + currentROMValue + "°");
        supportInfoText.setText("Support: " + supportString);
        levelInfoText.setText("Level: " + currentLevel);
        scoreText.setText("0 / 30");

        gameView.setROM(currentROMValue);

        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);
            gameView.setGameStarted(true);
        });


        gameView.setOnGameOverListener(success -> {
            runOnUiThread(() -> {
                Intent intent = new Intent(GameActivity.this, FeedbackActivity.class);
                intent.putExtra("score", currentScore);
                intent.putExtra("rom", currentROMValue + "°");
                intent.putExtra("support", supportString);
                intent.putExtra("level", currentLevel);
                intent.putExtra("success", success);
                startActivity(intent);
                finish();
            });
        });

        gameView.setOnScoreChangeListener(score -> {
            runOnUiThread(() -> {
                scoreText.setText(score + " / 30");
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    
    public void updateAngle(float newAngle) {
        if (gameView != null) {
            gameView.setArmAngle(newAngle);
        }
    }
}
