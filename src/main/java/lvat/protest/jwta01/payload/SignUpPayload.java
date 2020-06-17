package lvat.protest.jwta01.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpPayload implements PayloadRequest {
    @NotBlank()
    @Size(min = 4, max = 200)
    private String name;

    @NotBlank
    @Size(min = 4, max = 25)
    private String username;

    @NotBlank
    @Email(message = "email")
    @Size(max = 75)
    private String email;

    @Size(min = 4, max = 200)
    private String password;

    public SignUpPayload() {
    }

    public SignUpPayload(@NotBlank @Size(max = 200) String name, @NotBlank @Size(max = 25) String username,
                         @NotBlank @Email @Size(min = 12, max = 75) String email, @Size(min = 4, max = 200) String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
