package com.safeg.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User; // 또는 UserDetails 구현

import java.util.Collection;

public class CustomUserDetails extends User { // User를 상속받는 것이 편리

    private Long id; // users 테이블의 실제 id 값

    // 생성자 (기본 User 생성자 호출 후 id 필드 초기화)
    public CustomUserDetails(Long id, String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public CustomUserDetails(Long id, String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }

    // id 값을 가져올 게터 메서드
    public Long getId() {
        return id;
    }

    // (필요하다면 추가적인 유저 정보 필드를 여기에 더 추가할 수 있어)
}