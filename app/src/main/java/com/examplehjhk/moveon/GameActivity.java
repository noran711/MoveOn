package com.examplehjhk.moveon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;

public class GameActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

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

    // ===== USB Serial =====
    private UsbSerialPort usbPort;
    private SerialInputOutputManager usbIoManager;
    private final StringBuilder rxBuffer = new StringBuilder();
    private static final int BAUDRATE = 115200; // muss zu Serial.begin() am Arduino passen

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
            if (currentROMValue > 90) currentROMValue = 90;
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

        // Game Over -> Feedback
        gameView.setOnGameOverListener(success -> runOnUiThread(() -> {
            Intent intent = new Intent(GameActivity.this, FeedbackActivity.class);
            intent.putExtra("score", currentScore);
            intent.putExtra("rom", currentROMValue + "°");
            intent.putExtra("support", supportString);
            intent.putExtra("level", currentLevel);
            intent.putExtra("success", success);
            startActivity(intent);
            finish();
        }));

        // Score Update
        gameView.setOnScoreChangeListener(score -> runOnUiThread(() -> {
            currentScore = score;
            scoreText.setText(score + " / 30");
        }));

        // ===== USB Verbindung starten =====
        initUsbConnection();
    }

    // ===== USB Connection =====
    private void initUsbConnection() {
        UsbManager manager = (UsbManager) getSystemService(USB_SERVICE);
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (drivers.isEmpty()) {
            Toast.makeText(this, "Kein USB-Gerät gefunden.", Toast.LENGTH_SHORT).show();
            return;
        }

        UsbSerialDriver driver = drivers.get(0);

        // Achtung: kann null sein, wenn Android keine Berechtigung hat
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            Toast.makeText(this,
                    "Keine USB-Berechtigung. Bitte neu anstecken und Zugriff erlauben.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        usbPort = driver.getPorts().get(0);

        try {
            usbPort.open(connection);
            usbPort.setParameters(BAUDRATE, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            usbIoManager = new SerialInputOutputManager(usbPort, this);
            new Thread(usbIoManager).start();

            Toast.makeText(this, "USB verbunden ✅", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "USB Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ===== SerialInputOutputManager.Listener =====
    @Override
    public void onNewData(byte[] data) {
        // Daten kommen in Chunks -> wir sammeln bis \n
        String chunk = new String(data);
        rxBuffer.append(chunk);

        int idx;
        while ((idx = rxBuffer.indexOf("\n")) != -1) {
            String line = rxBuffer.substring(0, idx).trim();
            rxBuffer.delete(0, idx + 1);

            if (line.isEmpty()) continue;

            // Falls dein Serial Monitor Prefixe wie "->" zeigt:
            // 10:55... -> 54.7;330
            // Dann nehmen wir den Teil nach "->"
            int arrow = line.lastIndexOf("->");
            if (arrow != -1) {
                line = line.substring(arrow + 2).trim();
            }

            // Erwartet: "54.7;330"
            String[] parts = line.split(";");
            if (parts.length < 2) continue;

            try {
                float angle = Float.parseFloat(parts[0].trim());
                int potiRaw = Integer.parseInt(parts[1].trim());

                // ✅ Poti korrekt normieren (0..330 → 0..1)
                float slider = Math.max(0f, Math.min(1f, potiRaw / 330f));

                runOnUiThread(() -> {
                    updateAngle(angle);              // Winkel 0..90 → Herz hoch/runter
                    gameView.setThumbSlider(slider); // Slider → Herzgröße / Speed
                });

            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public void onRunError(Exception e) {
        runOnUiThread(() ->
                Toast.makeText(this, "USB Verbindung verloren: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (usbIoManager != null) {
            usbIoManager.stop();
            usbIoManager = null;
        }
        if (usbPort != null) {
            try { usbPort.close(); } catch (IOException ignored) {}
            usbPort = null;
        }
    }

    public void updateAngle(float newAngle) {
        if (gameView != null) {
            gameView.setArmAngle(newAngle);
        }
    }
}
