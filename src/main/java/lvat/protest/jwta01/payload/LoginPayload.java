package lvat.protest.jwta01.payload;

import javax.validation.constraints.NotBlank;

public class LoginPayload implements PayloadRequest {
    @NotBlank
    private String emailOrUsername;

    @NotBlank
    private String password;

    public LoginPayload() {
    }

    public LoginPayload(@NotBlank String emailOrUsername, @NotBlank String password) {
        this.emailOrUsername = emailOrUsername;
        this.password = password;
    }

    public String getEmailOrUsername() {
        return emailOrUsername;
    }

    public void setEmailOrUsername(String emailOrUsername) {
        this.emailOrUsername = emailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
