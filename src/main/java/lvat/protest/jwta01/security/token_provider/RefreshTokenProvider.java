package lvat.protest.jwta01.security.token_provider;

import io.jsonwebtoken.*;
import lvat.protest.jwta01.repository.RedisRepository;
import lvat.protest.jwta01.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Component
public class RefreshTokenProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenProvider.class);
    private static final String RT_TYPE_CLAIM_KEY = "typ";
    private static final String RT_TYPE_CLAIM_VALUE = "refresh-token";
    private final KeyUtil keyUtil;
    private final RedisRepository redisRepository;

    public RefreshTokenProvider(KeyUtil keyUtil, RedisRepository redisRepository) {
        this.keyUtil = keyUtil;
        this.redisRepository = redisRepository;
    }

    public String generateRefreshToken(String publicUserId) {
        Date expireTime = new Date((new Date()).getTime() + keyUtil.getRefreshTokenExpirationInMs());
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(publicUserId)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireTime)
                .addClaims(Collections.singletonMap(RT_TYPE_CLAIM_KEY, RT_TYPE_CLAIM_VALUE))
                .signWith(SignatureAlgorithm.RS512, keyUtil.getRefreshTokenPrivateKey()) //RSA
//                .signWith(SignatureAlgorithm.ES512, keyUtil.getrefreshTokenPrivateKey()) //EC
                .compact();
    }

    public Boolean removeRefreshToken(String refreshToken) {
        Claims body = getRefreshTokenBody(refreshToken);
        if (body == null) {
            return false;
        }
        String type = body.get(RT_TYPE_CLAIM_KEY, String.class);
        if (!type.equals(RT_TYPE_CLAIM_VALUE)) {
            return false;
        }
        return redisRepository.addRefreshToken(body.getId(), body.getExpiration());
    }

    public Boolean isValidRefreshToken(String refreshToken) {
        Claims body = getRefreshTokenBody(refreshToken);
        if (body == null) {
            return false;
        }
        String type = body.get(RT_TYPE_CLAIM_KEY, String.class);
        if (!type.equals(RT_TYPE_CLAIM_VALUE)) {
            return false;
        }
        return !redisRepository.getRefreshToken(body.getId());
    }

    private Claims getRefreshTokenBody(String refreshToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(keyUtil.getRefreshTokenPublicKey())
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (SignatureException ex) {
            LOGGER.error("Invalid refresh token signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid refresh token token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired refresh token token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported refresh token token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Refresh token claims string is empty.");
        }
        return null;

    }
}
