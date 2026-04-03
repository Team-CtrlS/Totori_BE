package ctrlS.totori.report.controller;

import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.report.dto.response.TotalReportResponse;
import ctrlS.totori.report.dto.response.WeeklyReportResponse;
import ctrlS.totori.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "레포트 API", description = "주간, 전체 레포트 조회")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "월별 레포트 조회", description = "월별 레포트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content)
    })
    @GetMapping("/week")
    public ResponseEntity<WeeklyReportResponse> getWeeklyReport(@AuthenticationPrincipal CustomUserPrincipal principal){
        WeeklyReportResponse response = reportService.getWeeklyReport(principal.memberId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 레포트 조회", description = "전체 레포트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content)
    })
    @GetMapping("/total")
    public ResponseEntity<TotalReportResponse> getTotalReport(@AuthenticationPrincipal CustomUserPrincipal principal){
        TotalReportResponse response = reportService.getTotalReport(principal.memberId());
        return ResponseEntity.ok(response);
    }
}
