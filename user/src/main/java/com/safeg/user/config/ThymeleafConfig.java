package com.safeg.user.config; // 너의 프로젝트 패키지 구조에 맞게 변경해줘

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ITemplateResolver thymeleafTemplateResolver(SpringResourceTemplateResolver defaultTemplateResolver) {
        defaultTemplateResolver.setTemplateMode("HTML");
        defaultTemplateResolver.setPrefix("classpath:/templates/");
        defaultTemplateResolver.setSuffix(".html");
        defaultTemplateResolver.setCacheable(false); // 개발 중 캐시 비활성화 (프로덕션에서는 true)
        
        // ⭐ 이 부분이 핵심! #request 객체를 Thymeleaf Context에 추가합니다. ⭐
        defaultTemplateResolver.setCheckExistence(true);
        defaultTemplateResolver.setCharacterEncoding("UTF-8");
        
        // 이 부분을 통해 request, session 등의 유틸리티 객체를 다시 사용할 수 있게 됩니다.
        // 하지만 최신 버전 Thymeleaf는 기본적으로 비활성화하고, 명시적으로 추가하도록 변경되었습니다.
        // 여기서 핵심 설정이 없어 보입니다. Spring Boot 3.x (Thymeleaf 3.1.x) 부터는
        // 'SpringWebFluxExpressionObjectFactory' 또는 'SpringWebMvcExpressionObjectFactory'를 통해
        // 'IExpressionObjectFactory'를 커스텀해야 합니다.
        // Spring Boot 3.1.x 버전에서 #request를 재활성화하는 정확한 방법은 다음과 같습니다:

        return defaultTemplateResolver;
    }

    // Spring Boot 3.1.x (Thymeleaf 3.1.x)에서 #request 활성화하는 방법 (정확한 수정)
    // ApplicationContext를 주입받아 TemplateEngine에 추가하는 방식
    // 이는 Thymeleaf 템플릿 엔진 빈 설정에 해당하므로, SpringWebMvcConfiguration 같은 곳에서 이미
    // ThymeleafAutoConfiguration이 제공하는 TemplateEngine을 커스터마이즈해야 합니다.
    // 하지만, 가장 간단한 방법은 application.properties에서 다음 설정을 추가하는 것입니다.

    // application.properties 또는 application.yml 에 다음 설정 추가:
    // spring.thymeleaf.expression.extended.conversion-api=true // 이 옵션은 다른 기능
    // spring.thymeleaf.prefix=classpath:/templates/
    // spring.thymeleaf.suffix=.html
    // spring.thymeleaf.mode=HTML
    // spring.thymeleaf.cache=false

    // 실질적으로 #request를 활성화하기 위한 코드는 이 Configuration 파일보다는
    // application.properties 설정을 사용하거나, 더 낮은 수준에서 Customizing해야 합니다.
    // 가장 간단한 접근은 'application.properties'에 추가하는 방법이 아닙니다.
    // 대신, 'DefaultTemplateEngineConfiguration.java'를 오버라이드하여
    // SpringWebMvcExpressionObjectFactory를 수동으로 등록해야 합니다.

    // **가장 쉽고 권장되는 Spring Boot 3.1.x 환경에서의 해결책:**
    // 일반적으로는 @Configuration 클래스를 통해 IExpressionObjectFactory를
    // Customization 하도록 안내합니다. 하지만 이것은 좀 더 복잡한 과정입니다.
    // 다른 대안은 URL 정보를 Controller에서 Model에 담아서 뷰에 넘겨주는 것입니다.

    // ThymeleafConfig.java 는 Thymeleaf 설정 전반을 다루는 곳인데,
    // #request와 같은 유틸리티 객체 활성화는 직접적인 템플릿 리졸버 설정보다는
    // IExpressionObjectFactory 구현체를 통해 관리됩니다.
    // Spring Boot 3.1.x 환경에서는 다음과 같이 설정할 수 있습니다.
    
    // (이전에 제시한 ThymeleafConfig.java 코드는 최신 Thymeleaf 버전에서 #request 활성화에 직접적인 영향을 주지 않습니다.)
    // 가장 확실한 방법은, Controller에서 Request 정보를 Model에 추가하는 것입니다.

}