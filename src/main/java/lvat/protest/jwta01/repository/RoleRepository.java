package lvat.protest.jwta01.repository;

import lvat.protest.jwta01.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(Role.RoleName roleName);

    @Query(value = "FROM Role r WHERE r.roleName='ROLE_ADMIN'")
    Role findRoleAdmin();

    @Query(value = "FROM Role r WHERE r.roleName='ROLE_STUDENT'")
    Role findRoleStudent();

    @Query(value = "FROM Role r WHERE r.roleName='ROLE_TEACHER'")
    Role findRoleTeacher();

    @Query(value = "FROM Role r WHERE r.roleName='ROLE_COMPANY'")
    Role findRoleCompany();
}
