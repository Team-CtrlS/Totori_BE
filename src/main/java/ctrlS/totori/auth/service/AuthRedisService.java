package ctrlS.totori.auth.service;

import ctrlS.totori.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRedisService {
    private static final String LOGOUT_VALUE = "logout";

    private final RedisUtil redisUtil;

    public void blacklistToken(String token, long expirationSeconds) {
        redisUtil.setDataExpire(token, LOGOUT_VALUE, expirationSeconds);
    }

    public boolean isBlacklisted(String token) {
        return redisUtil.hasKey(token);
    }
}
