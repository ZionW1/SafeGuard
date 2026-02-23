package com.safeg.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:/safe_guard/upload/";
        } else {
            return "/Users/pieck/Documents/upload/";
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = getUploadPath();
        // registry.addResourceHandler("/images/**")  // 웹에서 접근할 경로
        //         .addResourceLocations("file:/Users/pieck/Documents/upload/");  // 실제 파일 위치
        registry.addResourceHandler("/admin/images/**")
                .addResourceLocations("file:" + path)
                .setCachePeriod(0);
            
    }
}