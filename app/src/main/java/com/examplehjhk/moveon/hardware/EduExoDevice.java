package com.examplehjhk.moveon.hardware;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Represents the EduExo hardware device.
 * This class manages the MQTT connection to receive sensor data (angles and slider values)
 * from the physical exoskeleton device.
 */
public class EduExoDevice {

    private static final String TAG = "EduExoDevice";

    // MQTT connection parameters
    private final String broker;
    private final int port;
    private final String topic;

    // Client used for MQTT communication
    private final SimpleMqttClient mqtt;

    // Callback listener to notify the app about hardware changes
    private DeviceListener listener;

    /**
     * Constructor for the EduExo device.
     * @param broker The URL or IP of the MQTT broker.
     * @param port   The port number for the MQTT connection.
     * @param topic  The specific MQTT topic to subscribe to for data.
     */
    public EduExoDevice(String broker, int port, String topic) {
        this.broker = broker;
        this.port = port;
        this.topic = topic;

        // Initialize the client with a unique random client ID
        this.mqtt = new SimpleMqttClient(broker, port, UUID.randomUUID().toString());
    }

    /**
     * Attaches a listener to handle incoming hardware data.
     * @param listener Implementation of DeviceListener.
     */
    public void setListener(DeviceListener listener) {
        this.listener = listener;
    }

    /**
     * Checks if the device is currently connected to the MQTT broker.
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        return mqtt != null && mqtt.isConnected();
    }

    /**
     * Connects to the broker and automatically subscribes to the data topic.
     * @param activity The current Android activity.
     */
    public void connectAndSubscribe(Activity activity) {
        if (mqtt == null) return;

        // If already connected, skip connection and just subscribe
        if (mqtt.isConnected()) {
            subscribe(activity);
            return;
        }

        // Attempt to connect to the MQTT broker
        mqtt.connect(new SimpleMqttClient.MqttConnection(activity) {
            @Override
            public void onSuccess() {
                // Subscription happens only after a successful connection
                subscribe(activity);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "MQTT connect failed", error);
                // Notify the listener that the connection failed/dropped
                if (listener != null) listener.onDisconnected();
            }
        });
    }

    /**
     * Disconnects the device from the MQTT broker.
     */
    public void disconnect() {
        try {
            if (mqtt != null && mqtt.isConnected()) mqtt.disconnect();
        } catch (Exception ignored) {}
    }

    /**
     * Subscribes to the specific topic and handles incoming messages.
     * @param activity The current Android activity.
     */
    private void subscribe(Activity activity) {
        mqtt.subscribe(new SimpleMqttClient.MqttSubscription(activity, topic) {
            @Override
            public void onMessage(String t, String payload) {
                // Expected payload format (JSON): {"user":"Test","t":"55.0;330"}
                try {
                    JSONObject obj = new JSONObject(payload);
                    String data = obj.getString("t");

                    // Split the data string (e.g., "55.0;330") into angle and potentiometer raw value
                    String[] parts = data.split(";");
                    if (parts.length < 2) return;

                    float angle = Float.parseFloat(parts[0].trim());
                    int potiRaw = Integer.parseInt(parts[1].trim());

                    // Notify the listener about the updated hardware values
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
                // Notify the listener about the network/subscription error
                if (listener != null) listener.onDisconnected();
            }
        });
    }
}