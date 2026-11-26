package com.individual.device.config;

import com.individual.device.filter.DeviceAuthFilter;
import com.individual.device.repository.DeviceRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<DeviceAuthFilter> deviceAuthFilterRegistration(DeviceRepository repository) {
        DeviceAuthFilter filter = new DeviceAuthFilter(repository);
        FilterRegistrationBean<DeviceAuthFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addUrlPatterns("/device/*");
        bean.setOrder(1);
        return bean;
    }
}