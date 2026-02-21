package com.safeg.admin.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {
//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         // **여기에 실제 이미지가 저장되는 절대 경로를 넣어줘!**
//         // 컨트롤러에서 System.getProperty("user.dir") + "/upload-images/"에 저장했다면
//         // 여기도 정확히 그 경로여야 해.
//         // 예: "file:/Users/pieck/Documents/프로젝트이름/upload-images/"
//         registry.addResourceHandler("/images/**")
//                 .addResourceLocations("file:/Users/pieck/Documents/upload/images/"); // 마지막 슬래시 / 도 꼭 넣어줘
//     }
// }

@Configuration // ⬅️ 이 클래스가 Spring 설정 파일임을 알려주는 어노테이션이야.
public class WebConfig implements WebMvcConfigurer { // ⬅️ WebMvcConfigurer 인터페이스를 구현해야 해.

    // 이미지를 실제로 저장하는 경로를 여기에 적어줘야 해.
    // ImageUploadController의 uploadDir과 정확히 일치해야 해!
    // 예: "file:/Users/pieck/Documents/upload/images/"
    private final String imageUploadPath = "/Users/pieck/Documents/safeg/admin/src/main/resources/static/images/"; 

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 이 메서드를 오버라이드해서 정적 리소스 핸들러를 추가할 수 있어.
        // registry.addResourceHandler("요청_경로/**")
        //         .addResourceLocations("파일_시스템_경로_혹은_classpath_경로/");

        // CKEditor에서 반환된 이미지 URL이 '/images/**' 패턴으로 시작하면,
        // 실제로는 'file:///Users/pieck/Documents/upload/images/' 폴더에서 파일을 찾도록 설정하는 거야.
        registry.addResourceHandler("/images/**") // ⬅️ 웹에서 접근하는 URL 패턴
                .addResourceLocations("file:" + imageUploadPath); // ⬅️ 실제 파일이 저장된 절대 경로
        
        // 중요: 경로 끝에 '/'를 꼭 붙여야 해!
        // 예를 들어, imageUploadPath가 "/Users/pieck/Documents/upload/images" 라면
        // "file:/Users/pieck/Documents/upload/images/" 이렇게 되어야 해.
        
        // 만약 이미지 파일이 'src/main/resources/static' 아래에 있다면 이 설정이 필요 없지만,
        // 지금처럼 외부 경로에 저장한다면 이 설정이 필수적이야.
    }

}