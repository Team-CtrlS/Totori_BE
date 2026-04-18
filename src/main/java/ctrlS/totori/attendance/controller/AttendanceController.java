package ctrlS.totori.attendance.controller;

import ctrlS.totori.attendance.dto.AttendanceResponse;
import ctrlS.totori.attendance.service.AttendanceService;
import ctrlS.totori.global.response.dto.BaseResponse;
import ctrlS.totori.global.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출석 API")
@RestController
@RequestMapping("/api/child/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "출석 체크", description = "현재 로그인한 아동의 출석을 체크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출석 체크 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 없습니다.", content = @Content),
    })
    @PostMapping
    public BaseResponse<AttendanceResponse> checkAttendance(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return BaseResponse.ok(attendanceService.checkAttendance(principal.memberId()));
    }
}