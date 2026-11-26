package com.individual.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Device {
    private String deviceId;
    private String deviceName;
    private String secret;
}
