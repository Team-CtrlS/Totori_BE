package ctrlS.totori.auth.service;

import ctrlS.totori.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRedisService {
    private static final String LOGOUT_VALUE = "logout";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 14 * 24 * 60 * 60; // 14일

    private final RedisUtil redisUtil;

    public void blacklistToken(String token, long expirationSeconds) {
        redisUtil.setDataExpire(token, LOGOUT_VALUE, expirationSeconds);
    }

    public boolean isBlacklisted(String token) {
        return redisUtil.hasKey(token);
    }

    // 로그인 시 refresh token 저장
    public void saveRefreshToken(Long memberId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisUtil.setDataExpire(key, refreshToken, REFRESH_TOKEN_EXPIRE_SECONDS);
    }

    public String getRefreshToken(Long memberId) {
        return redisUtil.getData(REFRESH_TOKEN_PREFIX + memberId);
    }

    public void deleteRefreshToken(Long memberId) {
        redisUtil.deleteDate(REFRESH_TOKEN_PREFIX + memberId);
    }

    public boolean isValidRefreshToken(Long memberId, String refreshToken) {
        String storedToken = getRefreshToken(memberId);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}
