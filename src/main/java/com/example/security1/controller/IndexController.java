package com.example.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View 리턴
public class IndexController {

    // localhost:8080/ , localhost:8080
    @GetMapping({"", "/"})
    public @ResponseBody String index() {
        // mustache -> 기본폴더가 src/main/resources/
        // 디펜던시 등록 시 yml 뷰리졸버 기본설정: prefix - templates, suffix - .mustache
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user() {
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

    // Controller에서 매핑하기 전에 Security가 낚아챔 -> SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/login")
    public @ResponseBody String login() {
        return "login";
    }

    @GetMapping("/join")
    public @ResponseBody String join() {
        return "join";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String joinProc() {
        return "회원가입 완료됨!";
    }
}
