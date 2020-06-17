package lvat.protest.jwta01.repository;

import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private static final String AT_PREFIX = "accessToken-";
    private static final String RT_PREFIX = "refreshToken-";
    private final RedisTemplate<String, Boolean> redisTemplate;
    private final ValueOperations<String, Boolean> valueOperations;
    private final SetOperations<String, Boolean> setOperations;
    private final ListOperations<String, Boolean> listOperations;
    private final ZSetOperations<String, Boolean> zSetOperations;

    public RedisRepository(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
        valueOperations = this.redisTemplate.opsForValue();
        setOperations = this.redisTemplate.opsForSet();
        zSetOperations = this.redisTemplate.opsForZSet();
        listOperations = this.redisTemplate.opsForList();
    }

    public void set(String key, Boolean value) {
        valueOperations.set(key, value);
        redisTemplate.expire(key, 3, TimeUnit.MINUTES);
    }

    public Boolean get(String key) {
        return valueOperations.get(key);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public Boolean addAccessToken(String accessTokenId, Date expireDate) {
        String key = AT_PREFIX + accessTokenId;
        valueOperations.set(key, true);
        return redisTemplate.expireAt(key, expireDate);
    }

    public Boolean getAccessToken(String accessTokenId) {
        String key = AT_PREFIX + accessTokenId;
        return valueOperations.get(key) != null;
    }

    public Boolean addRefreshToken(String accessTokenId, Date expireDate) {
        String key = RT_PREFIX + accessTokenId;
        valueOperations.set(key, true);
        return redisTemplate.expireAt(key, expireDate);
    }

    public Boolean getRefreshToken(String accessTokenId) {
        String key = RT_PREFIX + accessTokenId;
        return valueOperations.get(key) != null;
    }
}
