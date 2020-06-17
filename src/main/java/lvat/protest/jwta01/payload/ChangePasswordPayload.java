package lvat.protest.jwta01.payload;

import javax.validation.constraints.Size;

public class ChangePasswordPayload implements PayloadRequest {
    private String publicUserId;

    @Size(min = 4, max = 200)
    private String oldPassword;

    @Size(min = 4, max = 200)
    private String newPassword;

    @Size(min = 4, max = 200)
    private String confirmedNewPassword;

    public ChangePasswordPayload() {
    }

    public ChangePasswordPayload(String publicUserId, @Size(min = 4, max = 200) String oldPassword, @Size(min = 4, max = 200) String newPassword, @Size(min = 4, max = 200) String confirmedNewPassword) {
        this.publicUserId = publicUserId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmedNewPassword = confirmedNewPassword;
    }

    public String getPublicUserId() {
        return publicUserId;
    }

    public void setPublicUserId(String publicUserId) {
        this.publicUserId = publicUserId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmedNewPassword() {
        return confirmedNewPassword;
    }

    public void setConfirmedNewPassword(String confirmedNewPassword) {
        this.confirmedNewPassword = confirmedNewPassword;
    }
}
