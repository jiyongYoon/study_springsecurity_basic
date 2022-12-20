package com.example.security1.auth;

// 시큐리티가 /login 주소요청이 오면 낚아채서 로그인 진행시킬 때
// 로그인이 완료가 되면 session에서 시큐리티 자신만의 session을 만들어줌. (Security ContextHolder)
// 오브젝트 => Authentication 타입 객체가 꼭 들어가야 함
// Authentication 안에는 User정보가 있어야 함
// User 오브젝트 타입 => UserDetails 타입 객체가 꼭 들어가야 함
// 정리하면,
// Security 로그인 시 Session 중 Security 고유의 Session을 만들어 주는데,
// 그 부분에는 Authentication(UserDetails)가 들어있음

import com.example.security1.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;

    // 일반 로그인 시 사용하는 객체 (Authentication에 넣을)
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth2 로그인 시 사용하는 객체 (Authentication에 넣을)
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // OAuth2User를 구현해서 구현해야하는 메서드 / 유저 정보를 key - value 형태로 들고 있기 때문에 Map 형태로.
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정 만료여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 만료기간이 지났는지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
