package ctrlS.totori.member.controller;

import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.member.dto.MemberMeResponse;
import ctrlS.totori.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberMeResponse> getMyInfo(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(memberService.getMyInfo(principal.getMemberId()));
    }

}
