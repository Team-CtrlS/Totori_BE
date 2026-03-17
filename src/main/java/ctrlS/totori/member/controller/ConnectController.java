package ctrlS.totori.member.controller;

import ctrlS.totori.member.dto.ConnectCodeResponse;
import ctrlS.totori.member.dto.ConnectRequest;
import ctrlS.totori.member.service.ConnectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/connect")
@RequiredArgsConstructor
public class ConnectController {
    private final ConnectService connectService;

    @PostMapping("/code")
    public ResponseEntity<ConnectCodeResponse> createCode(@AuthenticationPrincipal Long memberId) {
        String code = connectService.createConnectCode(memberId);

        return ResponseEntity.ok(new ConnectCodeResponse(code, 600));
    }

    @PostMapping
    public ResponseEntity<Void> linkChild(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody ConnectRequest request) {
        connectService.connectToChild(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
