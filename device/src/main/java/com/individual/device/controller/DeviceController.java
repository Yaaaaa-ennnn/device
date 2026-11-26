package com.individual.device.controller;

import com.individual.device.model.Device;
import com.individual.device.model.DeviceVO;
import com.individual.device.model.RegisterRequest;
import com.individual.device.model.RegisterResponse;
import com.individual.device.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /** 1. 注册：客户端自带 deviceId + deviceName，只返回 secret */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (isBlank(req.getDeviceId()) || isBlank(req.getDeviceName())) {
            return badRequest("deviceId and deviceName required");
        }
        RegisterResponse resp = deviceService.register(req);
        if (resp == null) {
            return ResponseEntity.status(409).body(Map.of("code", 409, "msg", "deviceId already exists"));
        }
        return ResponseEntity.ok(resp);
    }

    /** 2. 查看所有设备：不需要密钥，返回 deviceId + deviceName */
    @GetMapping("/list")
    public List<DeviceVO> list() {
        return deviceService.list();
    }

    /** 3. 查看单台设备：不需要密钥 */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        DeviceVO vo = deviceService.findVOById(id);
        if (vo == null) {
            return notFound();
        }
        return ResponseEntity.ok(vo);
    }

    /** 4. 改名：请求头带密钥，路径 id 与头里的 id 必须一致 */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody RegisterRequest req) {
        if (isBlank(req.getDeviceName())) {
            return badRequest("deviceName required");
        }
        Device device = deviceService.update(id, req);
        if (device == null) {
            return notFound();
        }
        return ResponseEntity.ok(Map.of("code", 200, "msg", "updated: " + id));
    }

    /** 5. 删除：请求头带密钥，只能删自己 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!deviceService.delete(id)) {
            return notFound();
        }
        return ResponseEntity.ok(Map.of("code", 200, "msg", "deleted: " + id));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private ResponseEntity<?> badRequest(String msg) {
        return ResponseEntity.badRequest().body(Map.of("code", 400, "msg", msg));
    }

    private ResponseEntity<?> notFound() {
        return ResponseEntity.status(404).body(Map.of("code", 404, "msg", "device not found"));
    }
}
