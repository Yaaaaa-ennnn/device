package com.individual.device.repository;

import com.individual.device.model.Device;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
public class DeviceRepository {
    private final Map<String, Device> store = new ConcurrentHashMap<>();

    public void save(Device device) {
        store.put(device.getDeviceId(), device);
    }

    public Device findById(String deviceId) {
        return store.get(deviceId);
    }

    public Collection<Device> findAll() {
        return store.values();
    }

    public void clear() {
        store.clear();
    }

    public void deleteById(String deviceId) {
        store.remove(deviceId);
    }

    public boolean existsById(String deviceId) {
        return store.containsKey(deviceId);
    }
}
