package com.example.security1.auth;

import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login")으로 요청이 오면 자동으로
// UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 메서드가 힐행됨
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 여기서 파라미터에 있는 username은
    // 로그인 페이지(loginForm)에서 받는 ID값(여기서는 username)과 동일한 파라미터명이어야 함
    // 만약 변수명이 달라지면 config에서 .usernameParameter로 바꾸어야 함
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        User userEntity = userRepository.findByUsername(username);
        System.out.println(userEntity);
        if(username != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
    // 리턴값은 시큐리티 session의 Authentication의 내부에 UserDetails로 담긴다
    // session(Authentication(UserDetails))
}
