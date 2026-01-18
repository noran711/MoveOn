package com.examplehjhk.moveon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView finalScoreText = findViewById(R.id.finalScoreText);
        TextView finalRomText = findViewById(R.id.finalRomText);
        TextView finalSupportText = findViewById(R.id.finalSupportText);
        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // Daten vom Intent empfangen
        int score = getIntent().getIntExtra("score", 0);
        String rom = getIntent().getStringExtra("rom");
        String support = getIntent().getStringExtra("support");

        finalScoreText.setText("Score: " + score);
        finalRomText.setText("ROM: " + rom);
        finalSupportText.setText("Support: " + support);

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
