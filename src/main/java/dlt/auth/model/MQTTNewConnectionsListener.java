package dlt.auth.model;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import br.uefs.larsid.extended.mapping.devices.enums.ExtendedTATUMethods;
import br.uefs.larsid.extended.mapping.devices.model.TATUMessage;
import br.uefs.larsid.extended.mapping.devices.services.IDevicePropertiesManager;
import br.uefs.larsid.extended.mapping.devices.tatu.DeviceWrapper;
import br.uefs.larsid.extended.mapping.devices.tatu.ExtendedTATUWrapper;
import br.ufba.dcc.wiser.soft_iot.entities.Device;

/**
 *
 * @author Uellington Damasceno
 */
public class MQTTNewConnectionsListener implements MqttCallback {

    private ClientMqttManager clientMqtt;
    private IDevicePropertiesManager deviceManager;
    private static final Logger logger = Logger.getLogger(MQTTNewConnectionsListener.class.getName());

    public void setClientMqtt(ClientMqttManager client) {
        this.clientMqtt = client;
    }

    public void setDeviceManager(IDevicePropertiesManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public void initialize() throws MqttException {
        this.clientMqtt.setCallback(this);
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - Listener topic: {0}", ExtendedTATUWrapper.getConnectionTopic());
        this.clientMqtt.subscribe(ExtendedTATUWrapper.getConnectionTopic());
    }

    public void disconnect() throws MqttException {
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - Disconnecting from topic: {0}", ExtendedTATUWrapper.getConnectionTopic());
        if (this.clientMqtt == null) {
            logger.log(Level.WARNING, "SOFT-IOT-DLT-AUTH - Client is not connected, cannot unsubscribe.");
            return;
        }
        this.clientMqtt.unsubscribe(ExtendedTATUWrapper.getConnectionTopic());
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.log(Level.WARNING, null, cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        TATUMessage tatuMessage = new TATUMessage(message.getPayload());
        if (!tatuMessage.getMethod().equals(ExtendedTATUMethods.CONNECT)) {
            logger.log(Level.WARNING, "SOFT-IOT-DLT-AUTH - Received message with unsupported method: {0}", tatuMessage.getMethod());
            return;
        }
        Map sDevice = new JSONObject(tatuMessage.getMessageContent())
                .getJSONObject("DEVICE").toMap();

        Device device = DeviceWrapper.toDevice(sDevice);
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - recive connect message from {0}", device.getId());

        String connackMessage = ExtendedTATUWrapper
                .buildConnackMessage(device.getId(), device.getId(), true);
        this.deviceManager.addDevice(device);
        String targetTopic = ExtendedTATUWrapper.getConnectionTopicResponse();
        logger.log(Level.INFO, "SOFT-IOT-DLT-AUTH - SEND: {0} to {1}", new String[]{connackMessage, targetTopic});
        this.clientMqtt.publish(targetTopic, connackMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
