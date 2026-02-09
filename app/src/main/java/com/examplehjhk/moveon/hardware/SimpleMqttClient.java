package com.examplehjhk.moveon.hardware;

import android.app.Activity;
import android.util.Log;

import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A wrapper class designed to simplify the usage of the HiveMQ MQTT library.
 * It automatically handles switching from background network threads back to the
 * Main (UI) thread for callbacks.
 */
public class SimpleMqttClient {

    //region Callback class definitions

    /**
     * Base abstract class for handling the results of asynchronous MQTT operations.
     * @param <T> The type of the acknowledgment message.
     */
    private static abstract class MqttOperationResult<T> implements BiConsumer<T, Throwable> {
        protected Activity activity;

        /**
         * @param activity The context used to run callbacks on the UI thread.
         */
        public MqttOperationResult(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void accept(T ack, Throwable throwable) {
            if (throwable == null) {
                // Operation was successful
                logSuccess();
                // Network operations happen on background threads;
                // we must use runOnUiThread to update any UI elements.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess();
                    }
                });
            } else {
                // An error occurred during the operation
                logError(throwable);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onError(throwable);
                    }
                });
            }
        }

        protected abstract void logSuccess();

        protected abstract void logError(Throwable error);

        public void onSuccess() {
            // Optional override for UI success logic
        }

        public void onError(Throwable error) {
            // Optional override for UI error logic
        }
    }

    /**
     * Specialized handler for MQTT connection attempts.
     */
    public static abstract class MqttConnection extends MqttOperationResult<Mqtt3ConnAck> {

        public MqttConnection(Activity activity) {
            super(activity);
        }

        @Override
        protected void logSuccess() {
            Log.d("MQTT", "Connection established");
        }

        @Override
        protected void logError(Throwable error) {
            Log.e("MQTT", "Unable to connect", error);
        }
    }

    /**
     * Specialized handler for subscriptions and incoming message processing.
     */
    public static abstract class MqttSubscription extends MqttOperationResult<Mqtt3SubAck> implements Consumer<Mqtt3Publish> {
        private final String topic;

        public String getTopic() {
            return topic;
        }

        public MqttSubscription(Activity activity, String topic) {
            super(activity);
            this.topic = topic;
        }

        /**
         * Callback triggered when a message arrives on the subscribed topic.
         */
        public abstract void onMessage(String topic, String payload);

        @Override
        protected void logSuccess() {
            Log.d("MQTT", String.format("Subscribed to '%s'", topic));
        }

        @Override
        protected void logError(Throwable error) {
            Log.e("MQTT", String.format("Unable to subscribe to '%s'", topic), error);
        }

        /**
         * Internal HiveMQ callback triggered when a new message is published.
         * Automatically routes the payload back to the UI thread.
         */
        @Override
        public void accept(Mqtt3Publish mqtt3Publish) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String topic = mqtt3Publish.getTopic().toString();
                    String payload = new String(mqtt3Publish.getPayloadAsBytes());

                    Log.d("MQTT", String.format("Received message from topic '%s':\n%s", topic, payload));
                    onMessage(topic, payload);
                }
            });
        }
    }

    /**
     * Specialized handler for publishing messages.
     */
    public static abstract class MqttPublish extends MqttOperationResult<Mqtt3Publish> {
        private final String topic;
        private final String payload;

        public String getTopic() {
            return topic;
        }

        public String getPayload() {
            return payload;
        }

        public MqttPublish(Activity activity, String topic, String payload) {
            super(activity);
            this.topic = topic;
            this.payload = payload;
        }

        @Override
        protected void logSuccess() {
            Log.d("MQTT", String.format("Published to '%s':\n%s", topic, payload));
        }

        @Override
        protected void logError(Throwable error) {
            Log.e("MQTT", String.format("Unable to publish to '%s'", topic), error);
        }
    }
    //endregion

    //region Properties
    Mqtt3AsyncClient client;
    String serverHost;
    int serverPort;
    String identifier;

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return true if the client is currently connected to the MQTT broker.
     */
    public boolean isConnected() {
        return client.getState() == MqttClientState.CONNECTED;
    }
    //endregion

    /**
     * Initializes the MQTT client with host, port, and a unique identifier.
     */
    public SimpleMqttClient(String serverHost, int serverPort, String clientIdentifier) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.identifier = clientIdentifier;

        this.client = Mqtt3Client.builder()
                .serverHost(serverHost)
                .serverPort(serverPort)
                .identifier(clientIdentifier)
                .buildAsync();
    }

    //region MQTT service methods

    /**
     * Initiates an asynchronous connection to the broker.
     */
    public void connect(MqttConnection conn) {
        this.client.connect().whenComplete(conn);
    }

    /**
     * Disconnects from the broker (Blocking call).
     */
    public void disconnect() {
        client.toBlocking().disconnect();
    }

    /**
     * Subscribes to a topic asynchronously and sets up the message callback.
     */
    public void subscribe(MqttSubscription sub) {
        client.subscribeWith()
                .topicFilter(sub.getTopic())
                .callback(sub)
                .send()
                .whenComplete(sub);
    }

    /**
     * Unsubscribes from a specific topic (Blocking call).
     */
    public void unsubscribe(String topic) {
        client.toBlocking().unsubscribeWith().topicFilter(topic).send();
    }

    /**
     * Publishes a message to a specific topic asynchronously with QoS 1.
     */
    public void publish(MqttPublish pub) {
        client.publishWith()
                .topic(pub.getTopic())
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(pub.getPayload().getBytes())
                .send()
                .whenComplete(pub);
    }
    //endregion
}