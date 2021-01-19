package dlt.auth.services;

import extended.tatu.wrapper.model.Device;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Uellington Damasceno
 */
public interface IDevicePropertiesManager {

    public void addDevice(Device device) throws IOException;

    public Optional<Device> getDevice(String deviceId) throws IOException;

    public List<Device> getAllDevices() throws IOException;

    public void updateDevice(String deviceId, Device device) throws IOException;

    public boolean removeDevice(String deviceId) throws IOException;

    public void removeAll() throws IOException;
}
