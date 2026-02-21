package ctrlS.totori.auth.controller;

import ctrlS.totori.global.security.JwtTokenProvider;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // 카카오 로그인 임시 확인 테스트
    // todo: 프론트 화면 구현 시 삭제
    @GetMapping("/login/success")
    public String loginSuccess(@RequestParam("token") String token,
                               @RequestParam(value = "profileCompleted", required = false, defaultValue = "false") boolean profileCompleted) {
        return "token=" + token + "\n" + "profileCompleted=" + profileCompleted;
    }

    // 카카오 처음 시작 시 온보딩 테스트
    @PostMapping("/test/onboarding")
    @Transactional
    public ResponseEntity<String> completeOnboarding(
            @RequestParam String token,
            @RequestParam Role role,
            @RequestParam String name,
            @RequestParam String birthDate
    ) {
        String memberIdStr = jwtTokenProvider.getUserPk(token); // 너희 메서드명에 맞게
        Long memberId = Long.valueOf(memberIdStr);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        member.completeProfile(role, name, LocalDate.parse(birthDate));
        return ResponseEntity.ok("OK");
    }
}
