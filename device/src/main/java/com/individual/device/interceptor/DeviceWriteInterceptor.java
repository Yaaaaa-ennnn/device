package com.individual.device.interceptor;

import com.individual.device.model.Device;
import com.individual.device.repository.DeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 写操作拦截：只管 PUT / DELETE。
 * 自己做完整校验，不依赖 DeviceAuthFilter 塞进来的 attribute：
 *   1) 必须带 X-Device-Id / X-Device-Secret，且与库里的 secret 一致，否则 401；
 *   2) /device/{id} 的 id 必须是自己，否则 403。
 * 注册（POST /device/register）与查询（GET）全部放行。
 */
@Component
public class DeviceWriteInterceptor implements HandlerInterceptor {

    private final DeviceRepository repository;

    public DeviceWriteInterceptor(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String method = request.getMethod();
        if (!"PUT".equals(method) && !"DELETE".equals(method)) {
            return true;
        }

        // 1. 身份校验：请求头 + 库里的 secret 比对
        String deviceId = request.getHeader("X-Device-Id");
        String secret = request.getHeader("X-Device-Secret");
        if (deviceId == null || deviceId.isBlank() || secret == null || secret.isBlank()) {
            writeError(response, 401, "missing auth header");
            return false;
        }

        Device device = repository.findById(deviceId);
        if (device == null || !secret.equals(device.getSecret())) {
            writeError(response, 401, "invalid device or secret");
            return false;
        }

        // 2. 越权检查：/device/{id} 的 id 必须是自己
        String uri = request.getRequestURI();
        String targetId = uri.substring("/device/".length());
        if (targetId.endsWith("/")) {
            targetId = targetId.substring(0, targetId.length() - 1);
        }
        if (!targetId.isEmpty() && !targetId.equals(device.getDeviceId())) {
            writeError(response, 403, "cannot operate other device");
            return false;
        }

        // 校验通过，把身份传给后面的 controller
        request.setAttribute("deviceId", deviceId);
        request.setAttribute("device", device);

        return true;
    }

    private void writeError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"msg\":\"" + msg + "\"}");
    }
}
