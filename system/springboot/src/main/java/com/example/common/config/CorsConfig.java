package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 创建UrlBasedCorsConfigurationSource对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 创建CorsConfiguration对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许所有来源
        corsConfiguration.addAllowedOrigin("*");
        // 允许所有请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许所有请求方法
        corsConfiguration.addAllowedMethod("*");
        // 注册跨域配置
        source.registerCorsConfiguration("/**", corsConfiguration);
        // 返回CorsFilter对象
        return new CorsFilter(source);
    }
}