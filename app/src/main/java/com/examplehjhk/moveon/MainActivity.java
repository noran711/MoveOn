package com.examplehjhk.moveon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.examplehjhk.moveon.hardware.mqtt.SimpleMqttClient;
import com.examplehjhk.moveon.hardware.mqtt.data.MqttMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String MQTT_BROKER_URI = "tcp://broker.hivemq.com:1883";
    private static final String DEVICE_DATA_TOPIC = "moveon/device/data";

    private SimpleMqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        mqttClient = new SimpleMqttClient("broker.hivemq.com", 1883, UUID.randomUUID().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToMqttBroker();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mqttClient.unsubscribe(DEVICE_DATA_TOPIC);
        mqttClient.disconnect();
    }

    private void connectToMqttBroker() {
        mqttClient.connect(new SimpleMqttClient.MqttConnection(this) {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "MQTT Connected", Toast.LENGTH_SHORT).show();
                subscribeToDeviceData();
            }

            @Override
            public void onError(Throwable error) {
                Log.e("MQTT", "Connection failed", error);
                Toast.makeText(MainActivity.this, "MQTT Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeToDeviceData() {
        mqttClient.subscribe(new SimpleMqttClient.MqttSubscription(this, DEVICE_DATA_TOPIC) {
            @Override
            public void onMessage(String topic, String payload) {
                try {
                    MqttMessage mqttMessage = deserializeMessage(payload);
                    float armAngle = mqttMessage.getArmAngle();
                    float sliderValue = mqttMessage.getSliderValue();

                    // --- HIER IST DER WICHTIGSTE PUNKT ---
                    // Implementieren Sie hier Ihre Spiellogik, die auf den neuen Winkel reagiert.
                    // z.B. updateBirdPosition(armAngle);
                    Log.d("MQTT_DATA", "Received Arm Angle: " + armAngle + ", Slider: " + sliderValue);

                } catch (JSONException e) {
                    Log.e("MQTT", "Failed to deserialize message", e);
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("MQTT", "Subscription failed", error);
            }
        });
    }

    private MqttMessage deserializeMessage(String json) throws JSONException {
        JSONObject jMessage = new JSONObject(json);
        float armAngle = (float) jMessage.getDouble("armAngle");
        float sliderValue = (float) jMessage.getDouble("sliderValue");
        return new MqttMessage(armAngle, sliderValue);
    }
}
