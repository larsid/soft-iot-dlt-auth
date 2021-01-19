package dlt.auth.model;

import dlt.auth.services.IDevicePropertiesManager;
import dlt.auth.services.IPIDEditor;
import extended.tatu.wrapper.model.Device;
import extended.tatu.wrapper.util.DeviceWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONArray;

/**
 *
 * @author Uellington Damasceno
 */
public class DevicePropertiesManager implements IDevicePropertiesManager {

    private String mappingDevicePID;
    private String propertyKey;
    private IPIDEditor pidEditor;

    public DevicePropertiesManager(String mappingDevicePID, String propertyKey) {
        this.mappingDevicePID = mappingDevicePID;
        this.propertyKey = propertyKey;
    }

    public void setPidEditor(IPIDEditor pidEditor) {
        this.pidEditor = pidEditor;
    }

    @Override
    public void addDevice(Device device) throws IOException {
        try {
            Optional<Object> property = pidEditor.getProperty(mappingDevicePID, propertyKey);
            String devicesConnected = (String) property.get();
            JSONArray devices = new JSONArray(devicesConnected);
            devices.put(DeviceWrapper.toJSONObject(device));
            this.pidEditor.updateProperty(mappingDevicePID, propertyKey, devices.toString());
        } catch (IOException ex) {
        }
    }

    @Override
    public List<Device> getAllDevices() throws IOException {
        return this.getDevicesInPIDFile();
    }

    @Override
    public Optional<Device> getDevice(String deviceId) throws IOException {
        return this.getAllDevices().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findAny();
    }

    @Override
    public void updateDevice(String deviceId, Device device) throws IOException {
        List<Device> devices = this.getDevicesInPIDFile();
        if (!devices.isEmpty()) {
            devices.stream()
                    .filter(currentDevice -> !currentDevice.getId().equals(deviceId))
                    .collect(Collectors.toList());
        }
        devices.add(device);
        String jsonArray = new JSONArray(devices).toString();
        this.pidEditor.updateProperty(mappingDevicePID, propertyKey, jsonArray);
    }

    @Override
    public boolean removeDevice(String deviceId) throws IOException {
        List<Device> devices = this.getDevicesInPIDFile();
        boolean wasRemoved = devices.removeIf(device -> device.getId().equals(deviceId));
        String jsonArray = new JSONArray(devices).toString();
        this.pidEditor.updateProperty(mappingDevicePID, propertyKey, jsonArray);
        return wasRemoved;
    }

    @Override
    public void removeAll() throws IOException {
        String emptyJSONArray = new JSONArray().toString();
        try {
            pidEditor.updateProperty(mappingDevicePID, propertyKey, emptyJSONArray);
        } catch (IOException ex) {
            Logger.getLogger(DevicePropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Device> getDevicesInPIDFile() throws IOException {
        try {
            Optional<Object> stringOptional = this.pidEditor.getProperty(mappingDevicePID, propertyKey);
            if (stringOptional.isPresent()) {
                String array = (String) stringOptional.get();
                return DeviceWrapper.getAllDevices(array);
            }
        } catch (IOException ex) {
            Map<String, Object> properties = new HashMap();
            String emptyJSONArray = new JSONArray().toString();
            properties.put(propertyKey, emptyJSONArray);
            properties.put("debugMode", false);
            pidEditor.createPIDFile(mappingDevicePID, properties);
        }
        return new ArrayList();
    }
}
