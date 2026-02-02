package com.safeg.admin.vo;

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

    public CustomUser(UserVO userVo) {
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
        return userVo.getAuthList().stream()
            .map( (auth) -> new SimpleGrantedAuthority(auth.getAuth()))
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
    
}