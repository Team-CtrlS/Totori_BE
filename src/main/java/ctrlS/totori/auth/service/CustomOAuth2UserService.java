package ctrlS.totori.auth.service;

import ctrlS.totori.auth.dto.OAuth2Attribute;
import ctrlS.totori.global.security.oauth.CustomOAuth2User;
import ctrlS.totori.member.entity.LoginType;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession session;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 카카오에서 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 서비스 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attribute attributes = OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        try {
            // 저장(여기서 Role 결정)
            Member member = saveOrUpdate(attributes.providerId(), attributes.name());

            // Security 세션에 저장할 정보 반환
            return new CustomOAuth2User(
                    member.getId(),
                    member.getRole().name(),
                    attributes.attributes(),
                    userNameAttributeName
            );
        } finally {
            session.removeAttribute("SOCIAL_LOGIN_ROLE");
        }
    }

    private Member saveOrUpdate(String providerId, String nickname) {
        String loginId = "KAKAO_" + providerId;

        // 이미 가입된 회원
        Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);
        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }

        // 신규 회원
        Role role = (Role) session.getAttribute("SOCIAL_LOGIN_ROLE");

        // 세션 만료됐거나 없으면 예외 발생
        if (role == null) {
            OAuth2Error oAuth2Error = new OAuth2Error(
                    "MISSING_ROLE_IN_SESSION",
                    "세션이 만료되었거나 역할 정보가 없습니다. 처음부터 다시 시도해주세요.",
                    null
            );
            throw new OAuth2AuthenticationException(oAuth2Error, oAuth2Error.toString());
        }

        Member member = Member.builder()
                .loginId(loginId)
                .name(nickname)
                .password(null)
                .role(role)
                .loginType(LoginType.KAKAO)
                .build();

        return memberRepository.save(member);
    }
}