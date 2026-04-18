package ctrlS.totori.global.security.oauth;

import ctrlS.totori.auth.service.AuthRedisService;
import ctrlS.totori.global.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRedisService authRedisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인된 사용자 정보 꺼내기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String memberId = String.valueOf(oAuth2User.getMemberId());
        String role = oAuth2User.getAuthorities().iterator().next().getAuthority();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(memberId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        authRedisService.saveRefreshToken(Long.valueOf(memberId), refreshToken);

        // 앱으로 리다이렉트할 주소 만들기(임의로 localhost로 보냄)
        // todo: 앱 연동 시 실제 서비스 도메인으로 redirect URL 변경
        String targetUrl = String.format(
                "/login/success?accessToken=%s&refreshToken=%s&role=%s",
                accessToken, refreshToken, role);

        // 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
