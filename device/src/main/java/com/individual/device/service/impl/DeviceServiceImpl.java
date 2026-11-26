package com.individual.device.service.impl;

import com.individual.device.model.Device;
import com.individual.device.model.DeviceVO;
import com.individual.device.model.RegisterRequest;
import com.individual.device.model.RegisterResponse;
import com.individual.device.repository.DeviceRepository;
import com.individual.device.service.DeviceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository repository;

    public DeviceServiceImpl(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegisterResponse register(RegisterRequest req) {
        String deviceId = req.getDeviceId().trim();
        if (repository.existsById(deviceId)) {
            return null;
        }
        String secret = UUID.randomUUID().toString().replace("-", "");
        repository.save(new Device(deviceId, req.getDeviceName().trim(), secret));
        return new RegisterResponse(secret);
    }

    @Override
    public Device update(String deviceId, RegisterRequest req) {
        Device device = repository.findById(deviceId);
        if (device == null) {
            return null;
        }
        device.setDeviceName(req.getDeviceName().trim());
        return device;
    }

    @Override
    public boolean delete(String deviceId) {
        if (!repository.existsById(deviceId)) {
            return false;
        }
        repository.deleteById(deviceId);
        return true;
    }

    @Override
    public List<DeviceVO> list() {
        return repository.findAll().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceVO findVOById(String deviceId) {
        Device device = repository.findById(deviceId);
        return device == null ? null : toVO(device);
    }

    @Override
    public Device findById(String deviceId) {
        return repository.findById(deviceId);
    }

    private DeviceVO toVO(Device d) {
        return new DeviceVO(d.getDeviceId(), d.getDeviceName());
    }
}
