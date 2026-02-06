package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;
import com.examplehjhk.moveon.hardware.SimpleMqttClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    // ===== UI =====
    private GameView gameView;
    private Button btnStart;
    private TextView scoreText;
    private TextView romInfoText;
    private TextView supportInfoText;
    private TextView levelInfoText;

    // ===== GAME STATE =====
    private int currentScore = 0;
    private int currentLevel = 1;

    // Basiswerte (werden aus SharedPrefs oder DB geladen)
    private int currentROMValue = 30;
    private int romIncreaseValue = 5;
    private String supportString = "10%";

    // Session timing
    private long sessionStartMs;

    private User currentUser;
    private DBHelper dbHelper;

    // ===== MQTT =====
    private SimpleMqttClient mqttClient;
    private static final String MQTT_BROKER = "broker.hivemq.com";
    private static final int MQTT_PORT = 1883;
    private static final String MQTT_TOPIC = "moveon/sensor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // ===== INIT =====
        dbHelper = new DBHelper(this);
        sessionStartMs = System.currentTimeMillis();

        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        currentLevel = getIntent().getIntExtra("nextLevel", 1);

        // ===== UI BINDING =====
        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);
        scoreText = findViewById(R.id.scoreText);
        romInfoText = findViewById(R.id.romInfoText);
        supportInfoText = findViewById(R.id.supportInfoText);
        levelInfoText = findViewById(R.id.levelInfoText);

        // Startwerte UI
        scoreText.setText("0 / 30");

        // ===== SETTINGS LADEN + ANWENDEN =====
        loadPatientSettings();
        applySettingsToUIAndGame();

        // ===== START BUTTON =====
        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE);
            gameView.setGameStarted(true);

            // Session Startzeit neu setzen, damit echte Spieldauer stimmt
            sessionStartMs = System.currentTimeMillis();
        });

        // ===== CALLBACKS =====
        gameView.setOnScoreChangeListener(score -> runOnUiThread(() -> {
            currentScore = score;
            scoreText.setText(score + " / 30");
        }));

        gameView.setOnGameOverListener(success -> runOnUiThread(() -> {
            // ROM im Feedback soll dem *aktuellen* Stand entsprechen
            int effectiveRomNow = calculateEffectiveRom();

            saveSession(success);

            Intent intent = new Intent(GameActivity.this, FeedbackActivity.class);
            intent.putExtra("user", currentUser);
            intent.putExtra("score", currentScore);
            intent.putExtra("rom", effectiveRomNow + "°");
            intent.putExtra("support", supportString);
            intent.putExtra("level", currentLevel);
            intent.putExtra("success", success);
            startActivity(intent);
            finish();
        }));

        // ===== MQTT INIT =====
        mqttClient = new SimpleMqttClient(MQTT_BROKER, MQTT_PORT, UUID.randomUUID().toString());
    }

    // =====================================================
    // SETTINGS LADEN (SharedPrefs + DB Override vom Therapeuten)
    // =====================================================
    private void loadPatientSettings() {

        // 1) Defaults aus SharedPreferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        currentROMValue = parseIntSafe(prefs.getString("rom", "30°"), 30);
        romIncreaseValue = parseIntSafe(prefs.getString("rom_increase", "5°"), 5);
        supportString = prefs.getString("support", "10%");

        // 2) DB Override: Werte, die der Therapeut gesetzt hat
        Cursor s = dbHelper.getPatientSettings(currentUser.username);
        if (s != null && s.moveToFirst()) {
            currentROMValue = s.getInt(s.getColumnIndexOrThrow("rom"));
            romIncreaseValue = s.getInt(s.getColumnIndexOrThrow("rom_increase"));

            int supportPercent = s.getInt(s.getColumnIndexOrThrow("support_percent"));
            supportString = supportPercent + "%";
        }
        if (s != null) s.close();
    }

    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s.replace("°", "").replace("%", "").trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    // =====================================================
    // ROM fürs Level berechnen
    // =====================================================
    private int calculateEffectiveRom() {
        int effective = currentROMValue + (currentLevel - 1) * romIncreaseValue;
        if (effective > 90) effective = 90;
        if (effective < 0) effective = 0;
        return effective;
    }

    // =====================================================
    // UI + GameView setzen (mit aktuellem effective ROM)
    // =====================================================
    private void applySettingsToUIAndGame() {
        int effectiveRom = calculateEffectiveRom();

        levelInfoText.setText("Level: " + currentLevel);
        romInfoText.setText("ROM: " + effectiveRom + "°");
        supportInfoText.setText("Support: " + supportString);

        gameView.setROM(effectiveRom);
    }

    // =====================================================
    // SESSION SAVE (in SQLite)
    // =====================================================
    private void saveSession(boolean success) {
        long end = System.currentTimeMillis();
        int durationSec = (int) ((end - sessionStartMs) / 1000);

        dbHelper.insertGameSession(
                currentUser.username,
                currentLevel,
                currentScore,
                success,
                sessionStartMs,
                end,
                durationSec
        );
    }

    // =====================================================
    // AUTO-UPDATE wenn Therapeut Werte geändert hat
    // =====================================================
    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();

        // ✅ Wenn der Therapeut ROM/ROM+ geändert hat -> neu laden
        // Optional: nur wenn noch nicht gestartet -> damit nichts "springt"
        // Wenn du "immer" willst, entferne die if-Abfrage.
        if (btnStart != null && btnStart.getVisibility() == View.VISIBLE) {
            loadPatientSettings();
            applySettingsToUIAndGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.pause();
    }

    // =====================================================
    // MQTT
    // =====================================================
    @Override
    protected void onStart() {
        super.onStart();
        connectAndSubscribeMQTT();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception ignored) {}
    }

    private void connectAndSubscribeMQTT() {
        if (mqttClient == null) return;

        if (mqttClient.isConnected()) {
            subscribeMQTT();
            return;
        }

        mqttClient.connect(new SimpleMqttClient.MqttConnection(this) {
            @Override
            public void onSuccess() {
                subscribeMQTT();
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT connect failed", error);
            }
        });
    }

    private void subscribeMQTT() {
        mqttClient.subscribe(new SimpleMqttClient.MqttSubscription(this, MQTT_TOPIC) {
            @Override
            public void onMessage(String topic, String payload) {
                // Expected: {"t":"55.0;330"}  (angle;potiRaw)
                try {
                    JSONObject obj = new JSONObject(payload);
                    String t = obj.getString("t");

                    String[] parts = t.split(";");
                    if (parts.length < 2) return;

                    float angle = Float.parseFloat(parts[0].trim());
                    int potiRaw = Integer.parseInt(parts[1].trim());

                    runOnUiThread(() -> {
                        gameView.setArmAngle(angle);
                        gameView.setPotiRaw(potiRaw);
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
}
