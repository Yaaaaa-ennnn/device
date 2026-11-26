package com.individual.device.model;

import lombok.Data;

@Data
public class RegisterRequest {
    /** 客户端自带的设备 id，全局唯一 */
    private String deviceId;
    private String deviceName;
}
