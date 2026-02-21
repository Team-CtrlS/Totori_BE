package ctrlS.totori.auth.service;

import ctrlS.totori.auth.dto.KakaoUserInfo;
import ctrlS.totori.member.entity.LoginType;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 카카오에서 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 데이터 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        KakaoUserInfo kakaoInfo = new KakaoUserInfo(attributes);

        // 3. DB에 저장할 ID 생성
        String loginId = "KAKAO_" + kakaoInfo.getId();
        String nickname = kakaoInfo.getNickname();

        // 4. 저장(회원이 아닌 경우 임시로 회원 생성)
        Member member = memberRepository.findByLoginId(loginId)
                .map(entity -> entity.updateName(nickname))
                .orElseGet(() -> Member.builder()
                        .loginId(loginId)
                        .password(null)
                        .name(nickname)
                        .role(null)
                        .loginType(LoginType.KAKAO)
                        .build());

        memberRepository.save(member);

        // todo: 프론트 화면 구현 시 삭제
        log.info("카카오 로그인 성공: {}", loginId);

        // 5. Security 세션에 저장할 정보 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                attributes,
                "id"
        );
    }
}