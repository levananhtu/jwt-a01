package lvat.protest.jwta01.payload;

public class ResetPasswordPayload implements PayloadResponse {
    private String rc;
    private String password;
    private String confirmedPassword;

    public ResetPasswordPayload() {
    }

    public ResetPasswordPayload(String rc, String password, String confirmedPassword) {
        this.rc = rc;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }
}
