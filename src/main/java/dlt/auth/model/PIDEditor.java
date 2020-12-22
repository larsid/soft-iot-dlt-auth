package dlt.auth.model;

import dlt.auth.services.IPIDEditor;
import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class PIDEditor implements IPIDEditor {

    private ConfigurationAdmin configurationAdmin;

    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    @Override
    public Optional<Map<String, Object>> getProperties(String pid) throws IOException {
        Dictionary<String, Object> properties = this.configurationAdmin.getConfiguration(pid).getProperties();
        return Optional.of(Collections.list(properties.keys())
                .stream()
                .collect(Collectors.toMap(Function.identity(), properties::get)));
    }

    @Override
    public Optional<Object> getProperty(String pid, String key) throws IOException {
        Optional<Map<String, Object>> optProperties = this.getProperties(pid);
        if (optProperties.isPresent()) {
            Map<String, Object> properties = optProperties.get();
            Object property = properties.get(key);
            return property == null ? Optional.empty() : Optional.of(property);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void createPIDFile(String pid) throws IOException {
        this.createPIDFile(pid, new HashMap());
    }

    @Override
    public void createPIDFile(String pid, Map<String, Object> properties) throws IOException {
        Configuration configuration = this.configurationAdmin.createFactoryConfiguration(pid);
        configuration.update(this.convertToHashtable(properties));
    }

    @Override
    public void deletePIDFile(String pid) throws IOException {
        this.configurationAdmin.getConfiguration(pid).delete();
    }

    @Override
    public void updateProperties(String pid, Map<String, Object> properties) throws IOException {
        Configuration configuration = this.configurationAdmin.getConfiguration(pid);
        configuration.update(this.convertToHashtable(properties));
    }

    @Override
    public void updateProperty(String pid, String key, Object property) throws IOException {
        Configuration configuration = this.configurationAdmin.getConfiguration(pid);
        Dictionary<String, Object> properties = configuration.getProperties();
        properties.remove(key);
        properties.put(key, property);
        configuration.update(properties);
    }

    private Hashtable<String, Object> convertToHashtable(Map<String, Object> properties) {
        Hashtable<String, Object> hashtable = new Hashtable();
        if (!properties.isEmpty()) {
            hashtable.putAll(properties);
        }
        return hashtable;
    }

    private void listProperties(String pid) throws IOException {
        this.getProperties(pid).ifPresent((map) -> {
            map.forEach((k, v) -> {
                System.out.println(k + " = " + v);
            });
        });
    }

}
