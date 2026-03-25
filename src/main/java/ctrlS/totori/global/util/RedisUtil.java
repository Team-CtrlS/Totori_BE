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
        stringRedisTemplate.opsForValue()
                .set(key, value, Duration.ofSeconds(durationSeconds));
    }

    public String getData(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public String getAndDeleteData(String key) {
        return stringRedisTemplate.opsForValue().getAndDelete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
