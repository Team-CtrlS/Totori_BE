package ctrlS.totori.service;

import ctrlS.totori.domain.member.LoginType;
import ctrlS.totori.domain.member.Member;
import ctrlS.totori.domain.member.MemberRepository;
import ctrlS.totori.dto.auth.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
}
