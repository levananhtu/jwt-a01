package lvat.protest.jwta01.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "User")
@Table(name = "user")
@EntityListeners(value = AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "public_user_id", unique = true, nullable = false, updatable = false)
    private String publicUserId;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    @Size(min = 4, max = 25)
    private String username;

    @NotBlank
    @Email
    @Column(name = "email", unique = true, nullable = false)
    @Size(min = 12, max = 75)
    private String email;

    @Size(max = 200)
    @Column(name = "password")
    private String password;

    @Column(name = "account_non_expired")
    private Boolean nonExpired;

    @Column(name = "account_non_locked")
    private Boolean nonLocked;

    @Column(name = "credentials_non_expired")
    private Boolean credentialsNonExpired;

    @Column(name = "enabled")
    private Boolean enabled;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public User() {
    }

    public User(@NotBlank @Size(max = 200) String name, @NotBlank @Size(min = 4, max = 25) String username,
                @NotBlank @Email @Size(min = 12, max = 75) String email, @Size(max = 200) String password,
                Boolean nonExpired, Boolean nonLocked,
                Boolean credentialsNonExpired, Boolean enabled,
                AuthProvider provider, String providerId) {
        this.publicUserId = UUID.randomUUID().toString();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nonExpired = nonExpired;
        this.nonLocked = nonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User(@NotBlank @Size(max = 200) String name, @NotBlank @Size(min = 4, max = 25) String username,
                @NotBlank @Email @Size(min = 12, max = 75) String email, @Size(max = 200) String password,
                AuthProvider provider, String providerId) {
        this.publicUserId = UUID.randomUUID().toString();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nonExpired = true;
        this.nonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User(@NotBlank @Size(max = 200) String name, @NotBlank @Size(min = 4, max = 25) String username,
                @NotBlank @Email @Size(min = 12, max = 75) String email, @Size(max = 200) String password,
                Boolean nonExpired, Boolean nonLocked,
                Boolean credentialsNonExpired, Boolean enabled) {
        this.publicUserId = UUID.randomUUID().toString();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nonExpired = nonExpired;
        this.nonLocked = nonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.provider = null;
        this.providerId = null;
    }

    public User(@NotBlank @Size(max = 200) String name, @NotBlank @Size(min = 4, max = 25) String username,
                @NotBlank @Email @Size(min = 12, max = 75) String email, @Size(max = 200) String password) {
        this.publicUserId = UUID.randomUUID().toString();
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nonExpired = true;
        this.nonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.provider = null;
        this.providerId = null;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Boolean getNonExpired() {
        return nonExpired;
    }

    public void setNonExpired(Boolean nonExpired) {
        this.nonExpired = nonExpired;
    }

    public Boolean getNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(Boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPublicUserId() {
        return publicUserId;
    }

    public void setPublicUserId(String publicUserId) {
        this.publicUserId = publicUserId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public enum AuthProvider {
        LOCAL,
        FACEBOOK,
        GOOGLE,
        GITHUB
    }

}
