package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.domain.Level;
import com.examplehjhk.moveon.domain.User;
import com.examplehjhk.moveon.hardware.SimpleMqttClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivityMQTT";

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

    private User currentUser;

    // ✅ UML: Level
    private Level activeLevel;

    // ===== MQTT =====
    private SimpleMqttClient client;
    private static final String MQTT_BROKER = "broker.hivemq.com";
    private static final int MQTT_PORT = 1883;
    private static final String MQTT_TOPIC = "moveon/sensor";

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

        // Level + User
        currentLevel = getIntent().getIntExtra("nextLevel", 1);
        currentUser = (User) getIntent().getSerializableExtra("user");

        // ✅ Level Objekt erstellen (Gameplay bleibt gleich: 30 Obstacles)
        activeLevel = new Level(currentLevel);
        activeLevel.setObstacleCount(30);

        // ROM parsen
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
            if (currentROMValue > 90) currentROMValue = 90;
        }

        // Update UI
        romInfoText.setText("ROM: " + currentROMValue + "°");
        supportInfoText.setText("Support: " + supportString);
        levelInfoText.setText("Level: " + currentLevel);

        // ✅ Score Anzeige dynamisch
        scoreText.setText("0 / " + activeLevel.getObstacleCount());

        // ✅ GameView konfigurieren
        gameView.setROM(currentROMValue);
        gameView.setMaxObstacles(activeLevel.getObstacleCount());

        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);
            gameView.setGameStarted(true);
        });

        gameView.setOnGameOverListener(success -> runOnUiThread(() -> {
            Intent intent = new Intent(GameActivity.this, FeedbackActivity.class);
            intent.putExtra("score", currentScore);
            intent.putExtra("rom", currentROMValue + "°");
            intent.putExtra("support", supportString);
            intent.putExtra("level", currentLevel);
            intent.putExtra("success", success);
            intent.putExtra("user", currentUser);
            startActivity(intent);
            finish();
        }));

        gameView.setOnScoreChangeListener(score -> runOnUiThread(() -> {
            currentScore = score;
            scoreText.setText(score + " / " + activeLevel.getObstacleCount());
        }));

        // MQTT Client erstellen
        client = new SimpleMqttClient(MQTT_BROKER, MQTT_PORT, UUID.randomUUID().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectAndSubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (client != null && client.isConnected()) client.disconnect();
        } catch (Exception ignored) {}
    }

    private void connectAndSubscribe() {
        if (client == null) return;

        if (client.isConnected()) {
            subscribe();
            return;
        }

        client.connect(new SimpleMqttClient.MqttConnection(this) {
            @Override
            public void onSuccess() {
                subscribe();
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT connect failed", error);
            }
        });
    }

    private void subscribe() {
        client.subscribe(new SimpleMqttClient.MqttSubscription(this, MQTT_TOPIC) {
            @Override
            public void onMessage(String topic, String payload) {
                // Erwartet: {"user":"Test","t":"55.0;330"}
                try {
                    JSONObject obj = new JSONObject(payload);
                    String t = obj.getString("t"); // "55.0;330"

                    String[] parts = t.split(";");
                    if (parts.length < 2) return;

                    float angle = Float.parseFloat(parts[0].trim());
                    int potiRaw = Integer.parseInt(parts[1].trim());

                    runOnUiThread(() -> {
                        gameView.setArmAngle(angle);
                        gameView.setPotiRaw(potiRaw); // -> ThumbSlider
                    });

                } catch (JSONException | NumberFormatException e) {
                    Log.e(TAG, "MQTT parse error: " + payload, e);
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT subscribe error", error);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.pause();
    }
}
