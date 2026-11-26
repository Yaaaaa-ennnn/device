package com.individual.device.filter;

import com.individual.device.model.Device;
import com.individual.device.repository.DeviceRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 身份过滤器：只管写操作（PUT / DELETE）。
 * 注册和所有查询（GET）不需要密钥，直接放行。
 */
public class DeviceAuthFilter extends OncePerRequestFilter {

    private final DeviceRepository repository;

    public DeviceAuthFilter(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 注册 + 所有查询：不验身份
        if ("/device/register".equals(uri) || "GET".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String deviceId = request.getHeader("X-Device-Id");
        String secret = request.getHeader("X-Device-Secret");

        if (deviceId == null || secret == null) {
            writeError(response, 401, "missing auth header");
            return;
        }

        Device device = repository.findById(deviceId);
        if (device == null || !secret.equals(device.getSecret())) {
            writeError(response, 401, "invalid device or secret");
            return;
        }

        request.setAttribute("deviceId", deviceId);
        request.setAttribute("device", device);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"msg\":\"" + msg + "\"}");
    }
}
