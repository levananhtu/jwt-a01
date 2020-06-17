package lvat.protest.jwta01.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/*
@Component
public class KeyUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyUtil.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtPublicKeyString}")
    private String jwtPublicKeyString;

    @Value("${app.jwtPrivateKeyString}")
    private String jwtPrivateKeyString;

    @Value("${app.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    @Value("${app.keyPairAlgorithm}")
    private String keyPairAlgorithm;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getJwtPublicKeyString() {
        return jwtPublicKeyString;
    }

    public String getJwtPrivateKeyString() {
        return jwtPrivateKeyString;
    }

    public Key getJwtPublicKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(jwtPublicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(keyPairAlgorithm);
            return kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Key getJwtPrivateKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(jwtPrivateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(keyPairAlgorithm);
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
}
*/
@Component
public class KeyUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyUtil.class);

    @Value("${app.accessTokenKeyPairAlgorithm}")
    private String accessTokenKeyPairAlgorithm;

    @Value("${app.accessTokenPublicKeyString}")
    private String accessTokenPublicKeyString;

    @Value("${app.accessTokenPrivateKeyString}")
    private String accessTokenPrivateKeyString;

    @Value("${app.accessTokenExpirationInMs}")
    private Long accessTokenExpirationInMs;

//////////////////////////////////////////////////////////////////////////////////////

    @Value("${app.refreshTokenKeyPairAlgorithm}")
    private String refreshTokenKeyPairAlgorithm;

    @Value("${app.refreshTokenPublicKeyString}")
    private String refreshTokenPublicKeyString;

    @Value("${app.refreshTokenPrivateKeyString}")
    private String refreshTokenPrivateKeyString;

    @Value("${app.refreshTokenExpirationInMs}")
    private Long refreshTokenExpirationInMs;

//////////////////////////////////////////////////////////////////////////////////////

    public String getAccessTokenKeyPairAlgorithm() {
        return accessTokenKeyPairAlgorithm;
    }

    public String getAccessTokenPublicKeyString() {
        return accessTokenPublicKeyString;
    }

    public String getAccessTokenPrivateKeyString() {
        return accessTokenPrivateKeyString;
    }

    public Key getAccessTokenPublicKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(accessTokenPublicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(accessTokenKeyPairAlgorithm);
            return kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Key getAccessTokenPrivateKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(accessTokenPrivateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(accessTokenKeyPairAlgorithm);
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Long getAccessTokenExpirationInMs() {
        return accessTokenExpirationInMs;
    }

//////////////////////////////////////////////////////////////////////////////////////

    public String getRefreshTokenKeyPairAlgorithm() {
        return refreshTokenKeyPairAlgorithm;
    }

    public String getRefreshTokenPublicKeyString() {
        return refreshTokenPublicKeyString;
    }

    public String getRefreshTokenPrivateKeyString() {
        return refreshTokenPrivateKeyString;
    }

    public Key getRefreshTokenPublicKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(refreshTokenPublicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(refreshTokenKeyPairAlgorithm);
            return kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Key getRefreshTokenPrivateKey() {
        try {
            byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(refreshTokenPrivateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance(refreshTokenKeyPairAlgorithm);
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            LOGGER.error(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public Long getRefreshTokenExpirationInMs() {
        return refreshTokenExpirationInMs;
    }


}