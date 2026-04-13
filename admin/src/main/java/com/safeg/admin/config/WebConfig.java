package com.safeg.admin.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.safeg.admin.vo.CommonData;

import lombok.extern.slf4j.Slf4j;

@Configuration // ⬅️ 이 클래스가 Spring 설정 파일임을 알려주는 어노테이션이야.
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    // 이미지를 실제로 저장하는 경로를 여기에 적어줘야 해.
    // ImageUploadController의 uploadDir과 정확히 일치해야 해!
    // 예: "file:/Users/pieck/Documents/upload/images/" /Users/pieck/Documents/safeg/admin/src/main/resources/static/images
    private final String imageUploadPath = "/Users/pieck/Documents/upload/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "";
        // System.out.println("path : " + path);
        try {
            path = CommonData.getUploadPath();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("addResourceHandlers " + " addResourceHandlers registry + " + registry);
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + path)
                .setCachePeriod(0);
    }
    
}