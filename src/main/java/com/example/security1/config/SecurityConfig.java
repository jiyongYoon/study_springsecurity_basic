package com.example.security1.config;

import com.example.security1.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 활성화하면 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화 -> Controller 매핑에 @Secured("ROLE_XX")를 하면 적용됨 // preAuthorize, postAuthorize 어노테이션 활성화 -> Controller 매핑에 @PreAuthorize("hasRole('ROLE_XX')")
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean // 해당 메서드에서 리턴되는 객체를 IoC로 등록해준다
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 해당 url은 인증이 필요하다
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
            .and()
                .formLogin()
                .loginPage("/loginForm") // 로그인 인증이 필요한 페이지는 기존에 403 error가 아니라 login 페이지로 자동 이동하게 된다
                .loginProcessingUrl("/login") // login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인 진행 -> 컨트롤러에 /login 매핑 안해도 됨
                .defaultSuccessUrl("/")
            .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함. -> 1. 코드 받기(인증) / 2. 엑세스 토큰 받기(권한생김) / 3. 사용자 프로필 정보 가져와서 / 4. 후처리(회원가입 절차를 추가로 가져가거나, 그냥 회원가입 시키기도 함)
                .userInfoEndpoint() // oauth 라이브러리를 사용하게 되면 로그인성공 시 받은 엑세스토근+사용자 정보를 바로 Service단으로 가져갈 수 있다.
                .userService(principalOauth2UserService) // DefaultOAuth2UserService를 상속한 클래스의 loadUser메서드에서 후처리가 가능해짐.
        ;

        // .loginPage인 /loginForm으로 와서 로그인하면 defaultSuccessUrl로 보내주는데,
        // 어떤 특정 페이지로 가려고 했는데 인증이 필요해서 로그인했을 경우, 로그인이 성공하면 바로 그 페이지로 보내줌

        return http.build();
    }
}
