package com.safeg.user.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.safeg.user.security.CustomAccessDeniedHandler;
import com.safeg.user.security.LoginFailureHandler;
import com.safeg.user.security.LoginSuccessHandler;
// import com.aloha.security6.service.UserDetailServiceImpl;
import com.safeg.user.service.UserDetailServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    // 스프링 시큐리티 설정 메소드

    public SecurityConfig(DataSource dataSource,
        UserDetailServiceImpl userDetailServiceImpl,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler,
        CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.dataSource = dataSource;
        this.userDetailServiceImpl = userDetailServiceImpl;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain : : : : : : : : :");
        // ✅ 인가 설정
        http.authorizeHttpRequests(auth -> auth
                                // .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                                .requestMatchers("/mypage", "/mypage/**").hasAnyRole("USER", "LEADER", "ADMIN")
                                // .requestMatchers("/apply", "/apply/**").hasAnyRole( "LEADER", "ADMIN")
                                .requestMatchers("/**").permitAll()
                                .anyRequest().permitAll());
        log.info("::::: SecurityFilterChain - 인가 설정 완료 :::::");
        // 폼 로그인 설정
        http.formLogin(login -> login.loginPage("/user01") // 로그인 페이지 경로
            .loginProcessingUrl("/login")  // 로그인 요청 경로
            .usernameParameter("userId") // 아이디 파라미터
            .passwordParameter("password")// 비밀번호 파라미터
            //.defaultSuccessUrl("/?success") // 로그인 성공 경로
            .successHandler(loginSuccessHandler) // 로그인 성공 처리자 설정
            // .failureUrl("/login?error") // 로그인 실패 경로
            .failureHandler(loginFailureHandler) // 로그인 실패 처리자 설정
            );

        // 사용자 정의 인증
        http.userDetailsService(userDetailServiceImpl);

        // 자동 로그인 설정
        http.rememberMe(me -> me.key("23emperor")
            .rememberMeParameter("auto-login")
            .tokenRepository(tokenRepository())
            .tokenValiditySeconds(60 * 60 * 24 * 7)); // 7일 유효시간 (초단위)

        //인증 예외 처리
        http.exceptionHandling(exception -> exception
            .accessDeniedHandler(customAccessDeniedHandler));

        http.logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃 요청 경로
                .logoutSuccessUrl("/") // 로그아웃 성공 시 url
                .invalidateHttpSession(true)  // 세션 초기화
                .deleteCookies("remember-id") // 로그아웃 시, 아이디 저장 쿠키 삭제
                // .logoutSuccessHandler("null") // 로그아웃 성공 처리자 설정
        );

        return http.build();
        
    }
    
    /**
     * 🍃 암호화 방식 빈 등록
     * @return
     */
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 🍃 AuthenticationManager 인증 관리자 빈 등록
     * @param authenticationConfiguration
     * @return
     * @throws Exception
    */
    @Bean
    public AuthenticationManager authenticationManager( 
                                    AuthenticationConfiguration authenticationConfiguration ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /**
     * 👮‍♂️🔐 사용자 인증 관리 빈 등록 메소드
     * JDBC 인증 방식
     * ✅ 데이터 소스 (URL, ID, PW) - application.properties
     * ✅ SQL 쿼리 등록         
     * ⭐ 사용자 인증 쿼리
     * ⭐ 사용자 권한 쿼리
     * @return
     */
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     JdbcUserDetailsManager userDetailsManager 
    //             = new JdbcUserDetailsManager(dataSource);

    //     // 사용자 인증 쿼리
    //     String sql1 = " SELECT username, password, enabled "
    //                 + " FROM user "
    //                 + " WHERE username = ? "
    //                 ;
    //     // 사용자 권한 쿼리
    //     String sql2 = " SELECT username, auth "
    //                 + " FROM user_auth "
    //                 + " WHERE username = ? "
    //                 ;
    //     userDetailsManager.setUsersByUsernameQuery(sql1);
    //     userDetailsManager.setAuthoritiesByUsernameQuery(sql2);
    //     return userDetailsManager;
    // }

    /**
    * 🍃 자동 로그인 저장소 빈 등록
    * ✅ 데이터 소스
    * ⭐ persistent_logins 테이블 생성
            create table persistent_logins (
                username varchar(64) not null
                , series varchar(64) primary key
                , token varchar(64) not null
                , last_used timestamp not null
            );
    * 🔄 자동 로그인 프로세스
    * ✅ 로그인 시 
    *     ➡ 👩‍💼(ID, 시리즈, 토큰) 저장
    * ✅ 로그아웃 시, 
    *     ➡ 👩‍💼(ID, 시리즈, 토큰) 삭제
    * @return
    */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        // JdbcTokenRepositoryImpl : 토큰 저장 데이터 베이스를 등록하는 객체
        JdbcTokenRepositoryImpl repositoryImpl = new JdbcTokenRepositoryImpl();
        // ✅ 토큰 저장소를 사용하는 데이터 소스 지정
        // - 시큐리티가 자동 로그인 프로세스를 처리하기 위한 DB를 지정합니다.
        repositoryImpl.setDataSource(dataSource);
        // 서버 실행시, 자동 로그인 테이블 자동 생성
        // repositoryImpl.setCreateTableOnStartup(false);

        // persistent_logins 테이블 생성
        try {
            repositoryImpl.getJdbcTemplate().execute(JdbcTokenRepositoryImpl.CREATE_TABLE_SQL);
        } 
        catch (BadSqlGrammarException e) {
            log.error("persistent_logins 테이블이 이미 존재합니다.");   
        }
        catch (Exception e) {
            log.error("자동 로그인 테이블 생성 중 , 예외 발생");
        }
        return repositoryImpl;
    }
}
