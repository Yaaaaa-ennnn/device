package com.individual.device.service;

import com.individual.device.model.Device;
import com.individual.device.model.DeviceVO;
import com.individual.device.model.RegisterRequest;
import com.individual.device.model.RegisterResponse;

import java.util.List;

public interface DeviceService {
    /** 注册；deviceId 已存在时返回 null */
    RegisterResponse register(RegisterRequest req);

    Device update(String deviceId, RegisterRequest req);

    boolean delete(String deviceId);

    List<DeviceVO> list();

    DeviceVO findVOById(String deviceId);

    Device findById(String deviceId);
}
