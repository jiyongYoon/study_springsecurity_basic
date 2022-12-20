package com.example.security1.controller;

import com.example.security1.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View 리턴
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
        @AuthenticationPrincipal PrincipalDetails userDetails2) { //@AuthenticationPrincipal를 통해서 Session 정보에 접근 가능
        // 원래는 UserDetails 오브젝트로 받아야 하는데, 현재 프로젝트에서는 PrincipalDetails가 UserDetails 를 상속했기 때문에 받기 가능

        System.out.println("/test/login =============");

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // Object 객체를 다운캐스팅
        System.out.println("principalDetails.getUser(): " + principalDetails.getUser());
        System.out.println("userDetails.getUser: " + userDetails2.getUser()); // 두 내용이 같음

        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication,
        @AuthenticationPrincipal OAuth2User oauth2User) { //@AuthenticationPrincipal를 통해서 Session 정보에 접근 가능

        System.out.println("/test/oauth/login =============");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("OAuth2User.getAttributes(): " + oAuth2User.getAttributes());
        System.out.println("oauth2User.getAttributes()r: " + oauth2User.getAttributes()); // 두 내용이 같음

        return "OAuth 세션 정보 확인하기";
    }

    // localhost:8080/ , localhost:8080
    @GetMapping({"", "/"})
    public @ResponseBody String index() {
        // mustache -> 기본폴더가 src/main/resources/
        // 디펜던시 등록 시 yml 뷰리졸버 기본설정: prefix - templates, suffix - .mustache
        return "index";
    }

    // PrincipalDetails에서 UserDetails랑 OAuth2User를 다 구현하고 나니
    // 일반 로그인, OAuth2 로그인 둘 다 처리가 가능해짐
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // Controller에서 매핑하기 전에 Security가 낚아챔
    // -> SecurityConfig 파일 생성 후 작동 안함.
    // -> 필터에서 login 페이지로 리다이렉트 해주기 때문.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); // DB 저장은 되나 시큐리티 로그인 불가능(PW 암호화가 되어야 함)
        return "redirect:/loginForm"; // loginForm이라는 메서드를 호출해줌
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }
}
