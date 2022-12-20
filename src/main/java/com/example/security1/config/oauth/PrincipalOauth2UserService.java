package com.example.security1.config.oauth;

import com.example.security1.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
        System.out.println("userRequest: " + userRequest);
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
        System.out.println("getAccessToken.getTokenValue: " + userRequest.getAccessToken().getTokenValue());
        System.out.println("getClientRegistration.getScopes: " + userRequest.getClientRegistration()
            .getScopes());
        System.out.println("getClientRegistration.getClientName: " + userRequest.getClientRegistration()
            .getClientName());
        System.out.println("getClientRegistration.getClientId: " + userRequest.getClientRegistration()
            .getClientId());
        System.out.println("getClientRegistration.getRegistrationId: " + userRequest.getClientRegistration()
            .getRegistrationId());
        System.out.println("getClientRegistration.getClientSecret: " + userRequest.getClientRegistration()
            .getClientSecret());
        System.out.println("getClientRegistration.getProviderDetails: " + userRequest.getClientRegistration()
            .getProviderDetails());
        System.out.println("getClientRegistration.getRedirectUri: " + userRequest.getClientRegistration()
            .getRedirectUri());
        System.out.println("super.loadUser(userRequest).getAttributes()" + super.loadUser(userRequest).getAttributes());
         */

        // userRequest 정보 받는 과정: 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code 리턴(OAuth-Client 라이브러리가 받음) -> code로 Access Token을 요청해서 받음
        // loadUser 메서드를 호출하여 회원 프로필을 받게 됨. 즉, 이 메서드는 구글로부터 회원프로필을 받아준다.

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_" + providerId;
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null) {
            userEntity = User.builder()
                .username(username)
                .password(null)
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(providerId)
                .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
    // 이 메서드 종료 시 @AuthenticationPrincipal 어노테이션이 만들어지고 해당 어노테이션으로 세션 접근 가능해짐
}
