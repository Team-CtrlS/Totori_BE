package ctrlS.totori.global.security.oauth;

import ctrlS.totori.global.security.JwtTokenProvider;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인된 사용자 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String loginId = "KAKAO_" + attributes.get("id");

        // DB에서 Role 확인
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("가입된 사용자가 없습니다."));
        boolean profileCompleted = member.getRole() != null && member.getBirthDate() != null;

        // 가입 전이면 GUEST, 가입 후면 실제 role
        String roleForToken = profileCompleted ? member.getRole().name() : "GUEST";

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(String.valueOf(member.getId()), roleForToken);

        // 앱으로 리다이렉트할 주소 만들기(임의로 localhost로 보냄)
        // todo: 앱으로 리다이렉트할 주소 넣기
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login/success")
                .queryParam("token", accessToken)
                .queryParam("profileCompleted", profileCompleted)
                .build().toUriString();

        // 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
