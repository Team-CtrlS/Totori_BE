package ctrlS.totori.member.controller;

import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.member.dto.ConnectCodeResponse;
import ctrlS.totori.member.dto.ConnectRequest;
import ctrlS.totori.member.service.ConnectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연결 API", description = "아동-보호자 계정 연결 API")
@RestController
@RequestMapping("/api/connect")
@RequiredArgsConstructor
public class ConnectController {
    private final ConnectService connectService;

    @Operation(summary = "연결 코드 생성 (아동용)", description = "아동이 보호자와 연결하기 위한 5자리 난수 코드를 생성합니다. (유효시간 10분)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 코드 생성 성공", content = @Content(schema = @Schema(implementation = ConnectCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값", content = @Content)
    })
    @PostMapping("/code")
    public ResponseEntity<ConnectCodeResponse> createCode(@AuthenticationPrincipal CustomUserPrincipal principal) {
        String code = connectService.createConnectCode(principal.memberId());

        return ResponseEntity.ok(new ConnectCodeResponse(code, 600));
    }

    @Operation(summary = "아동-보호자 계정 연결")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계정 연결 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청값 또는 유효하지 않은 연결 코드", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> linkChild(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ConnectRequest request) {
        connectService.connectToChild(principal.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
