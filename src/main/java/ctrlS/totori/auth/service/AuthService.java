package ctrlS.totori.auth.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.util.RedisUtil;
import ctrlS.totori.member.entity.LoginType;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.auth.dto.LoginRequest;
import ctrlS.totori.auth.dto.SignUpRequest;
import ctrlS.totori.auth.dto.TokenResponse;
import ctrlS.totori.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRedisService authRedisService;

    @Transactional
    public Long signUp(SignUpRequest request) {
        // 아이디 중복 검사
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // Entity 생성
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(encodePassword)
                .name(request.getName())
                .role(request.getRole())
                .loginType(LoginType.NATIVE)
                .build();

        // DB에 저장하고 회원 고유 Id 반환
        return memberRepository.save(member).getId();
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // DB에서 아이디로 회원 찾기
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_ID_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호까지 맞으면 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRole().name());

        // 토큰과 권한 정보 반환
        return new TokenResponse(token, member.getRole().name());
    }

    public void logout(String bearerToken) {
        String token = jwtTokenProvider.resolveToken(bearerToken);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            long expiration = jwtTokenProvider.getRemainingSeconds(token);
            authRedisService.blacklistToken(token, expiration);
        }
    }
}
