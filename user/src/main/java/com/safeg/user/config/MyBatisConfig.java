package com.safeg.user.config;

import com.safeg.user.config.StringToLocalDateTypeHandler; // ⭐⭐⭐ 새로 만든 TypeHandler 클래스의 경로! ⭐⭐⭐

import jakarta.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate; // LocalDate 임포트

@Configuration
public class MyBatisConfig {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void registerTypeHandlers() {
        TypeHandlerRegistry registry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        // ⭐⭐⭐ 이 라인을 추가/확인해 주세요! ⭐⭐⭐
        registry.register(LocalDate.class, StringToLocalDateTypeHandler.class);
        System.out.println(">>> Custom StringToLocalDateTypeHandler registered for LocalDate."); // 디버깅용 로그
    }
}