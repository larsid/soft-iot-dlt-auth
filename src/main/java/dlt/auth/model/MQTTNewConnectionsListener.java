package dlt.auth.model;

import br.uefs.larsid.extended.mapping.devices.enums.ExtendedTATUMethods;
import br.uefs.larsid.extended.mapping.devices.model.TATUMessage;
import br.uefs.larsid.extended.mapping.devices.services.IDevicePropertiesManager;
import br.uefs.larsid.extended.mapping.devices.tatu.DeviceWrapper;
import br.uefs.larsid.extended.mapping.devices.tatu.ExtendedTATUWrapper;
import br.ufba.dcc.wiser.soft_iot.entities.Device;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 *
 * @author Uellington Damasceno
 */
public class MQTTNewConnectionsListener implements MqttCallback {

    private ClientMqttManager clientMqtt;
    private IDevicePropertiesManager deviceManager;

    public void setClientMqtt(ClientMqttManager client) {
        this.clientMqtt = client;
    }

    public void setDeviceManager(IDevicePropertiesManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public void initialize() throws MqttException {
        this.clientMqtt.setCallback(this);
        this.clientMqtt.subscribe(ExtendedTATUWrapper.getConnectionTopic());
    }

    public void disconnect() throws MqttException {
        this.clientMqtt.unsubscribe(ExtendedTATUWrapper.getConnectionTopic());
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
            String targetTopic = ExtendedTATUWrapper.getConnectionTopicResponse();
            this.clientMqtt.publish(targetTopic, connackMessage);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

}
