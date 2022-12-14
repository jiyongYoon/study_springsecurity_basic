package com.example.security1.config;

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
public class SecurityConfig {

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
                .defaultSuccessUrl("/");

        // .loginPage인 /loginForm으로 와서 로그인하면 defaultSuccessUrl로 보내주는데,
        // 어떤 특정 페이지로 가려고 했는데 인증이 필요해서 로그인했을 경우, 로그인이 성공하면 바로 그 페이지로 보내줌

        return http.build();
    }
}
