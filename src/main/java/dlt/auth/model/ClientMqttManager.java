package dlt.auth.model;

import dlt.auth.services.IPublisher;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    
    public ClientMqttManager(String url, String port, String id, String username, String password) {
        this.serverId = id;
        this.username = username;
        this.password = password;
        this.uri = new StringBuilder()
                .append(url)
                .append(":")
                .append(port)
                .toString();
        
    }
    
    public void subscribe(String topic) throws MqttException{
        this.client.subscribe(topic);
    }
    
    public void unsubscribe(String topic) throws MqttException{
        this.client.unsubscribe(topic);
    }
    
    public void setCallback(MqttCallback callback) {
        this.client.setCallback(callback);
    }

    public void initialize() throws MqttException {
        if(this.client != null && this.client.isConnected()){
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
        this.client.connect(connection);
    }

    public void disconnect() {
        if (this.client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException ex) {
                Logger.getLogger(MQTTNewConnectionsListener.class.getName()).log(Level.SEVERE, null, ex);
            }
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
