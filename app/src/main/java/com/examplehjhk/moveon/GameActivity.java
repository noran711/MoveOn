package com.examplehjhk.moveon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private Button btnStart;
    private TextView scoreText;
    private TextView romInfoText;
    private TextView supportInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);
        scoreText = findViewById(R.id.scoreText);
        romInfoText = findViewById(R.id.romInfoText);
        supportInfoText = findViewById(R.id.supportInfoText);

        // ROM und Support aus SharedPreferences laden
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String romString = prefs.getString("rom", "90°");
        String supportString = prefs.getString("support", "10%");
        
        // Anzeige aktualisieren
        romInfoText.setText("ROM: " + romString);
        supportInfoText.setText("Support: " + supportString);
        
        // ROM-Wert für die Spiel-Logik umwandeln
        int romValue = 90;
        try {
            romValue = Integer.parseInt(romString.replace("°", "").trim());
        } catch (Exception e) {
            romValue = 90;
        }

        // ROM an GameView übergeben
        gameView.setROM(romValue);

        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);
            gameView.setGameStarted(true);
        });

        gameView.setOnGameOverListener(() -> {
            runOnUiThread(() -> {
                btnStart.setText("Restart");
                btnStart.setVisibility(View.VISIBLE);
            });
        });

        gameView.setOnScoreChangeListener(score -> {
            runOnUiThread(() -> {
                scoreText.setText("Score: " + score);
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
