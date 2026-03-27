package ctrlS.totori.global.config;

import ctrlS.totori.auth.service.CustomOAuth2UserService;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.security.JwtAuthenticationFilter;
import ctrlS.totori.global.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(ErrorCode.UNAUTHORIZED_ACCESS.getStatus());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    String.format(
                                            "{\"status\":%d,\"message\":\"%s\"}",
                                            ErrorCode.UNAUTHORIZED_ACCESS.getStatus(),
                                            ErrorCode.UNAUTHORIZED_ACCESS.getMessage()
                                    )
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(ErrorCode.ACCESS_DENIED.getStatus());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    String.format(
                                            "{\"status\":%d,\"message\":\"%s\"}",
                                            ErrorCode.ACCESS_DENIED.getStatus(),
                                            ErrorCode.ACCESS_DENIED.getMessage()
                                    )
                            );
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/login/success").permitAll()
                        .requestMatchers("/api/auth/complete").authenticated()
                        .requestMatchers("/api/parent/**").hasAuthority("PARENT")
                        .requestMatchers("/api/child/**").hasAuthority("CHILD")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );
        ;

        return http.build();
    }
}
