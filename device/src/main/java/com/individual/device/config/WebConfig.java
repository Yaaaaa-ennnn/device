package com.individual.device.config;

import com.individual.device.interceptor.DeviceWriteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final DeviceWriteInterceptor deviceWriteInterceptor;

    public WebConfig(DeviceWriteInterceptor deviceWriteInterceptor) {
        this.deviceWriteInterceptor = deviceWriteInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceWriteInterceptor)
                .addPathPatterns("/device/**");
    }
}
