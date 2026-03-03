package ctrlS.totori.global.security.oauth;

import ctrlS.totori.global.security.JwtTokenProvider;
import ctrlS.totori.member.entity.Member;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인된 사용자 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String targetId = authentication.getName();
        String role = oAuth2User.getAuthorities().iterator().next().getAuthority();

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(targetId, role);

        // 앱으로 리다이렉트할 주소 만들기(임의로 localhost로 보냄)
        // todo: 앱 연동 시 실제 서비스 도메인으로 redirect URL 변경
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login/success")
                .queryParam("token", token)
                .build().toUriString();

        // 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
