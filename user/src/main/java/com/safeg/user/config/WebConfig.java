package com.safeg.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/images/**")  // 웹에서 접근할 경로
        //         .addResourceLocations("file:/Users/pieck/Documents/upload/");  // 실제 파일 위치
        registry.addResourceHandler("/admin/images/**")
                .addResourceLocations("file:/Users/pieck/Documents/upload");

    }
}