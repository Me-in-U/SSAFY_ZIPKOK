package com.ssafy.house.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.house.interceptor.PerformanceInterceptor;

@Configuration
@MapperScan(basePackages = "com.ssafy.house.model.dao")
public class ApplicationConfig implements WebMvcConfigurer {
    @Autowired
    private PerformanceInterceptor performanceMonitorInterceptor;

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceMonitorInterceptor)
                .addPathPatterns("/member/**");
    }
}
