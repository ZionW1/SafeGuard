package com.safeg.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.safeg.user.util.LogControlInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LogControlInterceptor logControlInterceptor;

    public static String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:/safe_guard/upload/";
        } else if (os.contains("linux")) {
            return "/home/ubuntu/upload/";
        } else {
            return "/Users/pieck/Documents/upload/";
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = getUploadPath();
        // registry.addResourceHandler("/images/**")  // 웹에서 접근할 경로
        //         .addResourceLocations("file:/Users/pieck/Documents/upload/");  // 실제 파일 위치
        registry.addResourceHandler("/upload/**", "/admin/upload/**", "/admin/images/**")
                .addResourceLocations("file:" + path)
                .setCachePeriod(0);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logControlInterceptor)
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns("/static/**", "/public/**", "/css/**", "/js/**"); // 정적 자원은 제외
    }
}