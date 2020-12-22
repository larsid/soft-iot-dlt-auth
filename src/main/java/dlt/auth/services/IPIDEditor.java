package dlt.auth.services;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface IPIDEditor {
    
    public void createPIDFile(String pid) throws IOException;
    
    public void createPIDFile(String pid, Map<String, Object> properties) throws IOException;

    public void deletePIDFile(String pid) throws IOException;
    
    public Optional<Map<String, Object>> getProperties(String pid) throws IOException;
    
    public Optional<Object> getProperty(String pid, String key) throws IOException;
    
    public void updateProperties(String pid, Map<String, Object> properties) throws IOException;
    
    public void updateProperty(String pid, String key, Object property) throws IOException;
    
}
