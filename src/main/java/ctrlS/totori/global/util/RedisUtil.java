package ctrlS.totori.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    public void setDataExpire(String key, String value, long durationSeconds) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(durationSeconds);
        valueOperations.set(key, value, expireDuration);
    }

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public String getAndDeleteData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.getAndDelete(key);
    }

    public void setBlackList(String token, long expirationMinutes) {
        stringRedisTemplate.opsForValue().set(token, "logout", Duration.ofMinutes(expirationMinutes));
    }

    public boolean hasKeyBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }
}
