package ctrlS.totori.global.security;

import ctrlS.totori.member.entity.Role;

public record CustomUserPrincipal(Long memberId, Role role) {
}
