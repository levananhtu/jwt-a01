package lvat.protest.jwta01.payload;

public class TokenPairPayload implements PayloadResponse, PayloadRequest {
    private String accessToken;

    private String refreshToken;

    public TokenPairPayload() {
    }

    public TokenPairPayload(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
