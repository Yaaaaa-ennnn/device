package com.individual.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 注册响应：只返回密钥 */
@Data
@AllArgsConstructor
public class RegisterResponse {
    private String secret;
}
