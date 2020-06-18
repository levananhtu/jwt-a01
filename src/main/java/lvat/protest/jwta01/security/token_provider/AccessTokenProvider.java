package lvat.protest.jwta01.security.token_provider;

import io.jsonwebtoken.*;
import lvat.protest.jwta01.repository.RedisRepository;
import lvat.protest.jwta01.security.UserPrincipal;
import lvat.protest.jwta01.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Component
public class AccessTokenProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenProvider.class);
    private static final String AT_TYPE_CLAIM_KEY = "typ";
    private static final String AT_TYPE_CLAIM_VALUE = "access-token";
    private final KeyUtil keyUtil;
    private final RedisRepository redisRepository;

    public AccessTokenProvider(KeyUtil keyUtil, RedisRepository redisRepository) {
        this.keyUtil = keyUtil;
        this.redisRepository = redisRepository;
    }

    public String generateAccessToken(String publicUserId) {
        Date expireTime = new Date((new Date()).getTime() + keyUtil.getAccessTokenExpirationInMs());
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(publicUserId)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireTime)
                .addClaims(Collections.singletonMap(AT_TYPE_CLAIM_KEY, AT_TYPE_CLAIM_VALUE))
//                .signWith(SignatureAlgorithm.RS512, keyUtil.getJwtPrivateKey()) //RSA
                .signWith(SignatureAlgorithm.ES512, keyUtil.getAccessTokenPrivateKey()) //EC
                .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        String publicUserId = ((UserPrincipal) authentication.getPrincipal()).getPublicUserId();
        return generateAccessToken(publicUserId);
    }

    public Boolean removeAccessToken(String accessToken) {
        Claims body = getAccessTokenBody(accessToken);
        if (body == null) {
            return false;
        }
        String type = body.get(AT_TYPE_CLAIM_KEY, String.class);
        if (!type.equals(AT_TYPE_CLAIM_VALUE)) {
            return false;
        }
        return redisRepository.addAccessToken(body.getId(), body.getExpiration());
    }

    public Boolean isValidAccessToken(String accessToken) {
        Claims body = getAccessTokenBody(accessToken);
        if (body == null) {
            return false;
        }
        String type = body.get(AT_TYPE_CLAIM_KEY, String.class);
        if (!type.equals(AT_TYPE_CLAIM_VALUE)) {
            return false;
        }
        return !redisRepository.getAccessToken(body.getId());
    }

    private Claims getAccessTokenBody(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(keyUtil.getAccessTokenPublicKey())
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SignatureException ex) {
            LOGGER.error("Invalid access token signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid access token token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired access token token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported access token token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Access token claims string is empty.");
        }
        return null;

    }

    public String getPublicUserIdFromAccessToken(String accessToken) {
        Claims body = getAccessTokenBody(accessToken);
        assert body != null;
        return body.getSubject();
    }
}
