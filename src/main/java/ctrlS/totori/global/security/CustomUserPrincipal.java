package ctrlS.totori.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserPrincipal {
    private final Long memberId;
    private final String role;
}
