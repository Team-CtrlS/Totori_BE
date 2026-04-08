package ctrlS.totori.global.security;

import ctrlS.totori.auth.service.AuthRedisService;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.util.RedisUtil;
import ctrlS.totori.member.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRedisService authRedisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

        if (token != null) {
            if (authRedisService.isBlacklisted(token)) {
                throw new CustomAuthenticationException(ErrorCode.LOGGED_OUT_TOKEN);
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new CustomAuthenticationException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            Long memberId = Long.valueOf(jwtTokenProvider.getUserPk(token));
            String role = jwtTokenProvider.getRole(token);

            CustomUserPrincipal principal = new CustomUserPrincipal(memberId, Role.valueOf(role));

            var authorities = List.of(new SimpleGrantedAuthority(role));
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
