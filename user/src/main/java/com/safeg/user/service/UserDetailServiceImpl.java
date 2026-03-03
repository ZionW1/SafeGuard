package com.safeg.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;
import com.safeg.user.mapper.UserMapper;

import java.util.Optional;


import lombok.extern.slf4j.Slf4j;

/**
 *  🔐 UserDetailsService : 사용자 정보 불러오는 인터페이스
 *  ✅ 이 인터페이스를 구현하여, 사용자 정보를 로드하는 방법을 정의할 수 있습니다.
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(":::::::::: UserDetailServiceImpl ::::::::::");
        log.info("- 사용자 정의 인증을 위해, 사용자 정보 조회");
        log.info("- username : " + username);

// Optional<UserVO> userEntity = userMapper.findByUsername(username);
//     if (!userEntity.isPresent()) {
//         throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
//     }
//     UserVO user = userEntity.get();
//     return new org.springframework.security.core.userdetails.User(
//         user.getUserId(),
//         user.getPassword(), // 암호화된 비밀번호
//         getAuthorities(user)
//     );

        UserVO user = null;
        try {
            // 👩‍💼 사용자 정보 및 권한 조회
            user = userMapper.select(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( user == null ) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username);
        }

        // 🔐 CustomUser ➡ UserDetails
        CustomUser customUser = new CustomUser(user);
        log.info("- username : " + customUser);

        return customUser;
    }    
}