package ctrlS.totori.auth.service;

import ctrlS.totori.auth.dto.CompleteProfileRequest;
import ctrlS.totori.member.entity.LoginType;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.auth.dto.LoginRequest;
import ctrlS.totori.auth.dto.SignUpRequest;
import ctrlS.totori.auth.dto.TokenResponse;
import ctrlS.totori.global.security.JwtTokenProvider;
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

    @Transactional
    public Long signUp(SignUpRequest request) {
        // 아이디 중복 검사
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // Entity 생성
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(encodePassword)
                .name(request.getName())
                .birthDate(request.getBirthDate())
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
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호까지 맞으면 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRole().name());

        // 토큰과 권한 정보 반환
        return new TokenResponse(token, member.getRole().name());
    }

    @Transactional
    public void completeProfile(Long memberId, CompleteProfileRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        member.completeProfile(request.role(), request.name(), request.birthDate());
    }
}
