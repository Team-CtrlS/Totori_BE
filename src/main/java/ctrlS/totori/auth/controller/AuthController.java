package ctrlS.totori.auth.controller;

import ctrlS.totori.auth.dto.LoginRequest;
import ctrlS.totori.auth.dto.SignUpRequest;
import ctrlS.totori.auth.dto.TokenResponse;
import ctrlS.totori.auth.service.AuthService;
import ctrlS.totori.member.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "인증 API", description = "회원가입, 로그인, 소셜 로그인 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "자체 회원가입", description = "역할, 아이디, 비밀번호, 이름을 입력받아 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디", content = @Content)})
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자체 로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값", content = @Content),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치", content = @Content),
            @ApiResponse(responseCode = "404", description = "가입되지 않은 아이디", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "카카오 로그인", description = "역할을 전달받아 세션에 저장한 뒤 카카오 OAuth2 로그인 페이지로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값", content = @Content)
    })
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

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 토큰을 무효화(Redis Blacklist)하여 로그아웃 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 혹은 이미 로그아웃된 토큰", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        authService.logout(bearerToken);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 Authorization 헤더에 담아 요청하면 새 Access Token과 Refresh Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token", content = @Content)
    })
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        TokenResponse tokenResponse = authService.reissue(bearerToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
