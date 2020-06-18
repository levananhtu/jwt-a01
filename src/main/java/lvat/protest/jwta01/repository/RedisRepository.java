package lvat.protest.jwta01.repository;

import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private static final String AT_PREFIX = "accessToken-";
    private static final String RT_PREFIX = "refreshToken-";
    private static final String RP_PREFIX = "resetPasswordCode-";
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ValueOperations<String, Boolean> booleanValueOperations;
    private final SetOperations<String, Boolean> setOperations;
    private final ListOperations<String, Boolean> listOperations;
    private final ZSetOperations<String, Boolean> zSetOperations;
    private final ValueOperations<String, String> stringValueOperations;

    public RedisRepository(RedisTemplate<String, Boolean> booleanRedisTemplate, RedisTemplate<String, String> stringRedisTemplate) {
        this.booleanRedisTemplate = booleanRedisTemplate;
        booleanValueOperations = this.booleanRedisTemplate.opsForValue();
        setOperations = this.booleanRedisTemplate.opsForSet();
        zSetOperations = this.booleanRedisTemplate.opsForZSet();
        listOperations = this.booleanRedisTemplate.opsForList();
        this.stringRedisTemplate = stringRedisTemplate;
        stringValueOperations = this.stringRedisTemplate.opsForValue();
    }

    public void set(String key, Boolean value) {
        booleanValueOperations.set(key, value);
        booleanRedisTemplate.expire(key, 3, TimeUnit.MINUTES);
    }

    public Boolean get(String key) {
        return booleanValueOperations.get(key);
    }

    public Long getExpire(String key) {
        return booleanRedisTemplate.getExpire(key);
    }

    public Boolean addAccessToken(String accessTokenId, Date expireDate) {
        String key = AT_PREFIX + accessTokenId;
        booleanValueOperations.set(key, true);
        return booleanRedisTemplate.expireAt(key, expireDate);
    }

    public Boolean getAccessToken(String accessTokenId) {
        String key = AT_PREFIX + accessTokenId;
        return booleanValueOperations.get(key) != null;
    }

    public Boolean addRefreshToken(String accessTokenId, Date expireDate) {
        String key = RT_PREFIX + accessTokenId;
        booleanValueOperations.set(key, true);
        return booleanRedisTemplate.expireAt(key, expireDate);
    }

    public Boolean getRefreshToken(String accessTokenId) {
        String key = RT_PREFIX + accessTokenId;
        return booleanValueOperations.get(key) != null;
    }

    public void addResetPasswordCode(String resetPasswordCode, String publicUserId) {
        String key = RP_PREFIX + resetPasswordCode;
        stringValueOperations.set(key, publicUserId, 30, TimeUnit.MINUTES);
    }

    public String getPublicUserIdFromResetPasswordCode(String resetPasswordCode) {
        String key = RP_PREFIX + resetPasswordCode;
        return stringValueOperations.get(key);
    }

    public Boolean removeResetPasswordCode(String resetPasswordCode) {
        String key = RP_PREFIX + resetPasswordCode;
        return stringRedisTemplate.delete(key);
    }
}
