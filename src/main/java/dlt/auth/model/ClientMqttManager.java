package dlt.auth.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dlt.auth.services.IPublisher;

/**
 *
 * @author Uellington Damasceno
 */
public class ClientMqttManager implements IPublisher {

    private final String uri;
    private final String serverId;
    private final String username;
    private final String password;
    private MqttClient client;

    private final static Logger logger = Logger.getLogger(ClientMqttManager.class.getName());

    public ClientMqttManager(String url, Integer port, String id, String username, String password) {
        this.serverId = id;
        this.username = username;
        this.password = password;
        this.uri = new StringBuilder()
                .append(url)
                .append(":")
                .append(port)
                .toString();
    }

    public void subscribe(String topic) throws MqttException {
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - Subscribing to topic: {0}", topic);
        if (this.client == null || !this.client.isConnected()) {
            initialize();
        }
        this.client.subscribe(topic);
    }

    public void unsubscribe(String topic) throws MqttException {
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - Unsubscribing from topic: {0}", topic);
        if (this.client == null || !this.client.isConnected()) {
            initialize();
        }
        this.client.unsubscribe(topic);
    }

    public void setCallback(MqttCallback callback) {
        this.client.setCallback(callback);
    }

    public void initialize() throws MqttException {
        if (this.client != null && this.client.isConnected()) {
            return;
        }
        this.client = new MqttClient(this.uri, serverId);
        MqttConnectOptions connection = new MqttConnectOptions();
        if (!this.username.isEmpty()) {
            connection.setUserName(this.username);
        }
        if (!this.password.isEmpty()) {
            connection.setPassword(this.password.toCharArray());
        }
        logger.log(Level.INFO,
                "SOFT-IOT-DLT-AUTH - Connecting to MQTT broker at: {0}, {1}, {2}, {3}",
                new Object[]{this.uri, this.serverId, this.username, this.password});
        this.client.connect(connection);
    }

    public void disconnect() {
        if (this.client == null || !client.isConnected()) {
            logger.log(Level.WARNING, "SOFT-IOT-DLT-AUTH - Client is not connected, cannot disconnect.");
            return;
        }
        try {
            client.disconnect();
            logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - Disconnected from MQTT broker: {0}", this.uri);
        } catch (MqttException ex) {
            logger.log(Level.WARNING, null, ex);
        }
    }

    @Override
    public void publish(String topic, String message) throws MqttException {
        this.client.publish(topic, new MqttMessage(message.getBytes()));
    }

    @Override
    public void publish(String topic, byte[] message) throws MqttException {
        this.client.publish(topic, new MqttMessage(message));
    }

}
