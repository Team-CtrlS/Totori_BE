package ctrlS.totori.member.controller;

import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.member.dto.AcornResponse;
import ctrlS.totori.member.dto.MemberMeResponse;
import ctrlS.totori.member.dto.UpdateMemberRequest;
import ctrlS.totori.member.dto.UpdateMemberResponse;
import ctrlS.totori.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberMeResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 없습니다.", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<MemberMeResponse> getMyInfo(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(memberService.getMyInfo(principal.getMemberId()));
    }

    // 회원 도토리 개수 조회
    @Operation(summary = "내 도토리 개수 조회", description = "현재 로그인한 회원의 도토리 개수를 조회합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "도토리 개수 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcornResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 없습니다.", content = @Content)
    })
    @GetMapping("/me/acorns")
    public ResponseEntity<AcornResponse> getMyAcorn(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(memberService.getMyAcorn(principal.getMemberId()));
    }

    // 회원 정보 수정
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 정보를 수정합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "회원 정보 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값입니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 없습니다.", content = @Content)
    })
    @PatchMapping("/me")
    public ResponseEntity<UpdateMemberResponse> updateMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid UpdateMemberRequest request
            ) {
        return ResponseEntity.ok(memberService.updateMyInfo(principal.getMemberId(), request));
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원의 계정을 삭제합니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "회원 탈퇴 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 없습니다.", content = @Content)
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestHeader("Authorization") String bearerToken
    ) {
        memberService.deleteMyAccount(principal.getMemberId(), bearerToken);
        return ResponseEntity.noContent().build();
    }
}
