package dlt.auth.services;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author Uellington Damasceno
 */
public interface IPublisher {
    public void publish(String topic, String message) throws MqttException;
    public void publish(String topic, byte[] message) throws MqttException;
}
