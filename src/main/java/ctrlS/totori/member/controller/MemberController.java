package ctrlS.totori.member.controller;

import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.member.dto.AcornResponse;
import ctrlS.totori.member.dto.MemberMeResponse;
import ctrlS.totori.member.dto.UpdateMemberRequest;
import ctrlS.totori.member.dto.UpdateMemberResponse;
import ctrlS.totori.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberMeResponse> getMyInfo(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(memberService.getMyInfo(principal.getMemberId()));
    }

    // 회원 도토리 개수 조회
    @GetMapping("/me/acorns")
    public ResponseEntity<AcornResponse> getMyAcorn(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(memberService.getMyAcorn(principal.getMemberId()));
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<UpdateMemberResponse> updateMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid UpdateMemberRequest request
            ) {
        return ResponseEntity.ok(memberService.updateMyInfo(principal.getMemberId(), request));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestHeader("Authorization") String bearerToken
    ) {
        memberService.deleteMyAccount(principal.getMemberId(), bearerToken);
        return ResponseEntity.noContent().build();
    }
}
