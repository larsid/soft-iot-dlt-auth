package dlt.auth.model;

import dlt.auth.services.IDevicePropertiesManager;
import extended.tatu.wrapper.enums.ExtendedTATUMethods;
import extended.tatu.wrapper.model.Device;
import extended.tatu.wrapper.model.TATUMessage;
import extended.tatu.wrapper.util.DeviceWrapper;
import extended.tatu.wrapper.util.ExtendedTATUWrapper;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 *
 * @author Uellington Damasceno
 */
public class MQTTNewConnectionsListener implements MqttCallback {
    
    private final String uri;
    private final String serverId;
    private final String username;
    private final String password;
    private MqttClient client;
    private IDevicePropertiesManager deviceManager;
    
    public MQTTNewConnectionsListener(String url, String port, String id, String username, String password) {
        this.serverId = id;
        this.username = username;
        this.password = password;
        this.uri = new StringBuilder()
                .append(url)
                .append(":")
                .append(port)
                .toString();
    }
    
    public void setDeviceManager(IDevicePropertiesManager deviceManager) {
        this.deviceManager = deviceManager;
    }
    
    public void initialize() throws MqttException {
        this.client = new MqttClient(this.uri, serverId);
        MqttConnectOptions connection = new MqttConnectOptions();
        if (!this.username.isEmpty()) {
            connection.setUserName(this.username);
        }
        if (!this.password.isEmpty()) {
            connection.setPassword(this.password.toCharArray());
        }
        this.client.connect(connection);
        this.client.setCallback(this);
        this.client.subscribe(ExtendedTATUWrapper.getConnectionTopic());
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
    public void connectionLost(Throwable cause) {
        Logger.getLogger(MQTTNewConnectionsListener.class.getName()).log(Level.SEVERE, null, cause);
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        TATUMessage tatuMessage = new TATUMessage(message.getPayload());
        if (tatuMessage.getMethod().equals(ExtendedTATUMethods.CONNECT)) {
            Map sDevice = new JSONObject(tatuMessage.getMessageContent())
                    .getJSONObject("DEVICE").toMap();
            
            Device device = DeviceWrapper.toDevice(sDevice);
            
            String connackMessage = ExtendedTATUWrapper
                    .buildConnackMessage(device.getId(), device.getId(), true);
            this.deviceManager.addDevice(device);
            MqttMessage response = new MqttMessage(connackMessage.getBytes());
            this.client.publish(ExtendedTATUWrapper.getConnectionTopicResponse(), response);
        }
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    
}
