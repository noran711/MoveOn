package com.examplehjhk.moveon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);

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
