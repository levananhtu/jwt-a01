package lvat.protest.jwta01.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Role")
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false)
    private RoleName roleName;

    public Role() {
    }

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            Role that = (Role) obj;
            return that.roleId.equals(this.roleId) || that.roleName.equals(this.roleName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, roleName);
    }

    public enum RoleName {
        ROLE_ADMIN("ROLE_ADMIN"),
        ROLE_STUDENT("ROLE_STUDENT"),
        ROLE_TEACHER("ROLE_TEACHER"),
        ROLE_COMPANY("ROLE_COMPANY");

        private String role;

        RoleName(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
