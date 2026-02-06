package com.examplehjhk.moveon.hardware;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.util.UUID;

public class EduExoDevice {

    private static final String TAG = "EduExoDevice";

    private final String broker;
    private final int port;
    private final String topic;

    private final SimpleMqttClient mqtt;
    private DeviceListener listener;

    public EduExoDevice(String broker, int port, String topic) {
        this.broker = broker;
        this.port = port;
        this.topic = topic;

        this.mqtt = new SimpleMqttClient(broker, port, UUID.randomUUID().toString());
    }

    public void setListener(DeviceListener listener) {
        this.listener = listener;
    }

    public boolean isConnected() {
        return mqtt != null && mqtt.isConnected();
    }

    public void connectAndSubscribe(Activity activity) {
        if (mqtt == null) return;

        if (mqtt.isConnected()) {
            subscribe(activity);
            return;
        }

        mqtt.connect(new SimpleMqttClient.MqttConnection(activity) {
            @Override
            public void onSuccess() {
                subscribe(activity);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT connect failed", error);
                if (listener != null) listener.onDisconnected();
            }
        });
    }

    public void disconnect() {
        try {
            if (mqtt != null && mqtt.isConnected()) mqtt.disconnect();
        } catch (Exception ignored) {}
    }

    private void subscribe(Activity activity) {
        mqtt.subscribe(new SimpleMqttClient.MqttSubscription(activity, topic) {
            @Override
            public void onMessage(String t, String payload) {
                // Erwartet: {"user":"Test","t":"55.0;330"}
                try {
                    JSONObject obj = new JSONObject(payload);
                    String data = obj.getString("t");

                    String[] parts = data.split(";");
                    if (parts.length < 2) return;

                    float angle = Float.parseFloat(parts[0].trim());
                    int potiRaw = Integer.parseInt(parts[1].trim());

                    if (listener != null) {
                        listener.onAngleChanged(angle);
                        listener.onSliderChanged(potiRaw);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Parse error: " + payload, e);
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT subscribe error", error);
                if (listener != null) listener.onDisconnected();
            }
        });
    }
}
