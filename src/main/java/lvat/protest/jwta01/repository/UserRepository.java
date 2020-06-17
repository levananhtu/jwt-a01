package lvat.protest.jwta01.repository;

import lvat.protest.jwta01.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "FROM User u WHERE (u.username=:usernameOrEmail OR u.email=:usernameOrEmail) AND u.password=:password")
    Optional<User> findByCredentials(@Param(value = "usernameOrEmail") String usernameOrEmail, @Param(value = "password") String password);

    @Query(value = "FROM User u WHERE (u.username=:usernameOrEmail OR u.email=:usernameOrEmail) AND u.password=:password")
    Boolean existsByCredentials(@Param(value = "usernameOrEmail") String usernameOrEmail, @Param(value = "password") String password);

    Boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByUsernameOrEmail(String var1, String var2);

    Optional<User> findByPublicUserId(String publicUserId);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
