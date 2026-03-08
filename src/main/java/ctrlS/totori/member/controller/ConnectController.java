package ctrlS.totori.member.controller;

import ctrlS.totori.member.dto.ConnectRequest;
import ctrlS.totori.member.service.ConnectService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Map<String, String>> createCode(@AuthenticationPrincipal Long memberId) {
        String code = connectService.createConnectCode(memberId);

        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        response.put("validTime", "600");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/link")
    public ResponseEntity<String> linkChild(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ConnectRequest request) {
        String childName = connectService.connectToChild(memberId, request);
        return ResponseEntity.ok(childName + " 아동과 성공적으로 연결되었습니다.");
    }
}
