package com.safeg.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.UserDetailMapper;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.UserVO;
import com.safeg.admin.vo.Users;

import lombok.extern.slf4j.Slf4j;

/**
 *  🔐 UserDetailsService : 사용자 정보 불러오는 인터페이스
 *  ✅ 이 인터페이스를 구현하여, 사용자 정보를 로드하는 방법을 정의할 수 있습니다.
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(":::::::::: UserDetailServiceImpl ::::::::::");
        log.info("- 사용자 정의 인증을 위해, 사용자 정보 조회");
        log.info("- username 1 : " + username);

        UserVO user = null;
        try {
            // 👩‍💼 사용자 정보 및 권한 조회
            user = userDetailMapper.select(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( user == null ) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username);
        }

        // 🔐 CustomUser ➡ UserDetails
        log.info("- getPassword 1 : " + user.getPassword());

        CustomUser customUser = new CustomUser(user);
        log.info("- username 2 : " + customUser);

        return customUser;
    }
    
}