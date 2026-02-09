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

/**
 * Main Game Activity that handles the gameplay loop, parameter management,
 * and real-time sensor data reception via MQTT.
 */
public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    // UI Components
    private GameView gameView;          // Custom view responsible for rendering the heart and obstacles
    private Button btnStart;            // Button to begin the game session
    private TextView scoreText;         // Displays current score
    private TextView romInfoText;       // Displays current ROM requirement
    private TextView supportInfoText;   // Displays current assistance/support percentage
    private TextView levelInfoText;     // Displays the current game level

    // Game State Variables
    private int currentScore = 0;       // Points earned in the current session
    private int currentLevel = 1;       // The difficulty level currently being played

    // Base therapy values
    private int currentROMValue = 30;   // Starting ROM in degrees
    private int romIncreaseValue = 5;   // How much ROM increases per level
    private String supportString = "10%"; // Percentage of robotic assistance

    // Session timing for analytics
    private long sessionStartMs;

    private User currentUser;           // The currently logged-in patient
    private DBHelper dbHelper;          // SQLite helper for persistent storage

    // MQTT Networking
    private SimpleMqttClient mqttClient;
    private static final String MQTT_BROKER = "broker.hivemq.com";
    private static final int MQTT_PORT = 1883;
    private static final String MQTT_TOPIC = "moveon/sensor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Hide the standard Action Bar for a full-screen experience
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Initialization
        dbHelper = new DBHelper(this);
        sessionStartMs = System.currentTimeMillis();

        // Verify that a user is logged in
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Determine which level to load
        currentLevel = getIntent().getIntExtra("nextLevel", 1);

        // UI Binding
        gameView = findViewById(R.id.gameView);
        btnStart = findViewById(R.id.btnStart);
        scoreText = findViewById(R.id.scoreText);
        romInfoText = findViewById(R.id.romInfoText);
        supportInfoText = findViewById(R.id.supportInfoText);
        levelInfoText = findViewById(R.id.levelInfoText);

        scoreText.setText("0 / 30");

        // Load and Apply Therapy Parameters
        loadPatientSettings();
        applySettingsToUIAndGame();

        // Start Button Setup
        btnStart.setOnClickListener(v -> {
            btnStart.setVisibility(View.GONE); // Hide button once game starts
            gameView.setGameStarted(true);

            // Reset start time to accurately track the actual play duration
            sessionStartMs = System.currentTimeMillis();
        });

        // Game Logic Callbacks

        // Updates the UI whenever the score changes inside the GameView
        gameView.setOnScoreChangeListener(score -> runOnUiThread(() -> {
            currentScore = score;
            scoreText.setText(score + " / 30");
        }));

        // Handles the end of the game (victory or defeat)
        gameView.setOnGameOverListener(success -> runOnUiThread(() -> {
            // Calculate final ROM for accurate feedback reporting
            int effectiveRomNow = calculateEffectiveRom();

            saveSession(success); // Save results to local database

            // Navigate to the Feedback screen with session results
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

        // MQTT Setup
        mqttClient = new SimpleMqttClient(MQTT_BROKER, MQTT_PORT, UUID.randomUUID().toString());
    }

    /**
     * Loads patient therapy settings from SharedPreferences
     * and overrides them with values set by a therapist in the database.
     */
    private void loadPatientSettings() {
        // Load defaults from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        currentROMValue = parseIntSafe(prefs.getString("rom", "30°"), 30);
        romIncreaseValue = parseIntSafe(prefs.getString("rom_increase", "5°"), 5);
        supportString = prefs.getString("support", "10%");

        // Database Override: Check if the therapist has assigned specific values
        Cursor s = dbHelper.getPatientSettings(currentUser.username);
        if (s != null && s.moveToFirst()) {
            currentROMValue = s.getInt(s.getColumnIndexOrThrow("rom"));
            romIncreaseValue = s.getInt(s.getColumnIndexOrThrow("rom_increase"));

            int supportPercent = s.getInt(s.getColumnIndexOrThrow("support_percent"));
            supportString = supportPercent + "%";
        }
        if (s != null) s.close();
    }

    /**
     * Safely parses an integer from a string, removing symbols like ° or %.
     */
    private int parseIntSafe(String s, int fallback) {
        try {
            return Integer.parseInt(s.replace("°", "").replace("%", "").trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Calculates the ROM required for the current level.
     * Higher levels require a larger movement angle, capped at 90 degrees.
     */
    private int calculateEffectiveRom() {
        int effective = currentROMValue + (currentLevel - 1) * romIncreaseValue;
        if (effective > 90) effective = 90;
        if (effective < 0) effective = 0;
        return effective;
    }

    /**
     * Updates the UI labels and configures the GameView logic with current settings.
     */
    private void applySettingsToUIAndGame() {
        int effectiveRom = calculateEffectiveRom();

        levelInfoText.setText("Level: " + currentLevel);
        romInfoText.setText("ROM: " + effectiveRom + "°");
        supportInfoText.setText("Support: " + supportString);

        gameView.setROM(effectiveRom);
    }

    /**
     * Persists the finished game session data into the SQLite database.
     */
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

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resume();

        // Refresh settings on resume in case the therapist updated them while the app was backgrounded
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

    // MQTT Network Lifecycle Management

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

    /**
     * Initiates connection to the MQTT broker and subscribes to sensor data.
     */
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

    /**
     * Subscribes to the sensor topic and parses incoming JSON payloads.
     */
    private void subscribeMQTT() {
        mqttClient.subscribe(new SimpleMqttClient.MqttSubscription(this, MQTT_TOPIC) {
            @Override
            public void onMessage(String topic, String payload) {
                // Expected JSON format: {"t":"55.0;330"} -> (angle_float;poti_raw_int)
                try {
                    JSONObject obj = new JSONObject(payload);
                    String t = obj.getString("t");

                    String[] parts = t.split(";");
                    if (parts.length < 2) return;

                    float angle = Float.parseFloat(parts[0].trim());
                    int potiRaw = Integer.parseInt(parts[1].trim());

                    // Update the GameView with new hardware data on the UI thread
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