package ctrlS.totori.auth.controller;

import ctrlS.totori.auth.dto.LoginRequest;
import ctrlS.totori.auth.dto.SignUpRequest;
import ctrlS.totori.auth.dto.TokenResponse;
import ctrlS.totori.auth.service.AuthService;
import ctrlS.totori.member.entity.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/social/kakao")
    public void startKakaoLogin(
            @RequestParam Role role,
            HttpServletResponse response,
            HttpSession session
            ) throws IOException {

        // 역할을 세션에 잠시 저장
        session.setAttribute("SOCIAL_LOGIN_ROLE", role);

        // 카카오 로그인 주소로 리다이렉트
        response.sendRedirect("/oauth2/authorization/kakao");
    }
}
