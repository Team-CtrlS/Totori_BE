package ctrlS.totori.auth.service;

import ctrlS.totori.auth.dto.LoginResponse;
import ctrlS.totori.connect.repository.ParentChildRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.LoginType;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.auth.dto.LoginRequest;
import ctrlS.totori.auth.dto.SignUpRequest;
import ctrlS.totori.auth.dto.TokenResponse;
import ctrlS.totori.global.security.JwtTokenProvider;
import ctrlS.totori.member.repository.MemberStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRedisService authRedisService;
    private final ParentChildRepository parentChildRepository;

    @Transactional
    public void signUp(SignUpRequest request) {
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

        // DB에 저장
        Member savedMember = memberRepository.save(member);
        memberStatRepository.save(MemberStat.builder().member(savedMember).build());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // DB에서 아이디로 회원 찾기
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_ID_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String memberId = String.valueOf(member.getId());
        String role = String.valueOf(member.getRole());

        // 비밀번호까지 맞으면 JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(memberId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        authRedisService.saveRefreshToken(member.getId(), refreshToken);

        Boolean hasConnected = null;
        if (member.getRole() == Role.PARENT) {
            hasConnected = parentChildRepository.existsByParent_Id(member.getId());
        }

        // 토큰과 권한 정보 반환
        return new LoginResponse(accessToken, refreshToken, role, hasConnected);
    }

    public void logout(String bearerToken) {
        String token = jwtTokenProvider.resolveToken(bearerToken);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            long expiration = jwtTokenProvider.getRemainingSeconds(token);
            authRedisService.blacklistToken(token, expiration);

            Long memberId = Long.valueOf(jwtTokenProvider.getUserPk(token));
            authRedisService.deleteRefreshToken(memberId);
        }
    }

    @Transactional(readOnly = true)
    public TokenResponse reissue(String bearerToken) {
        String refreshToken = jwtTokenProvider.resolveToken(bearerToken);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long memberId = Long.valueOf(jwtTokenProvider.getUserPk(refreshToken));

        if (!authRedisService.isValidRefreshToken(memberId, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String role = member.getRole().name();

        String newAccessToken = jwtTokenProvider.createAccessToken(String.valueOf(memberId), role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));

        authRedisService.saveRefreshToken(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken, role);
    }
}
