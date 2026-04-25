package com.safeg.user.vo;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomUser implements UserDetails {

    // 사용자 DTO
    private UserVO userVo;
    private Long id;
    private String userId;
    private String userNm;
    public CustomUser(UserVO userVo) {
        this.userVo = userVo;
        this.id = userVo.getId(); // ⭐ userVo에서 id 값을 받아 CustomUser의 id 초기화 ⭐
        this.userId = userVo.getUserId(); // ⭐ userVo에서 id 값을 받아 CustomUser의 id 초기화 ⭐
        this.userNm = userVo.getUserNm(); // ⭐ 생성자에서 값 초기화
    }

    // ⭐ id 값을 가져올 게터 메서드 추가 ⭐
    public Long getId() {
        return id;
    }

    public void setUserVo(UserVO userVo) {
        this.userVo = userVo;
    }

    /**
     * 🔐 권한 정보 메소드
     * ✅ UserDetails 를 CustomUser 로 구현하여,
     *    Spring Security 의 User 대신 사용자 정의 인증 객체(CustomUser)로 적용
     * ⚠ CustomUser 적용 시, 권한을 사용할 때는 'ROLE_' 붙여서 사용해야한다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("CustomUser - getAuthorities() 호출 : " + userVo.getAuthList());
        return userVo.getAuthList().stream()
            .map( (auth) -> new SimpleGrantedAuthority(auth.getAuth()) )
            .collect(Collectors.toList());
    }

    

    @Override
    public String getPassword() {
        return userVo.getPassword();
    }

    @Override
    public String getUsername() {
        return userVo.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userVo.getEnabled() == 0 ? false : true;
    }
    
    // (선택 사항: 디버깅을 위해 toString() 메서드를 오버라이드하면 좋아)
    @Override
    public String toString() {
        return "CustomUser{" +
            "id=" + id +
            ", userId='" + getUsername() + '\'' +
            ", userName='" + getUserNm() + '\'' +
            ", authorities=" + getAuthorities() +
            '}';
    }

    @ToString.Include
    public String getBankNm() {
        return userVo.getBankNm();
    }

    @ToString.Include
    public String getAccountNumber() {
        return userVo.getAccountNumber();
    }

    @ToString.Include
    public String getDepositor() {
        return userVo.getDepositor();
    }

    @ToString.Include
    public String getPhoneHash() {
        return userVo.getPhoneHash();
    }

    @ToString.Include
    public String getEmail() {
        return userVo.getEmail();
    }

    @ToString.Include
    public String getFullAddress() {
        return userVo.getFullAddress();
    }

    @ToString.Include
    public String getSavedName() {
        return userVo.getSavedName();
    }

    @ToString.Include
    public String getUserNm() {
        return userVo.getUserNm();
    }
}