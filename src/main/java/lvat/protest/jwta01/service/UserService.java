package lvat.protest.jwta01.service;

import lvat.protest.jwta01.entity.Role;
import lvat.protest.jwta01.entity.User;
import lvat.protest.jwta01.exception.UserConflictException;
import lvat.protest.jwta01.exception.UserNotFoundException;
import lvat.protest.jwta01.repository.RoleRepository;
import lvat.protest.jwta01.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> createTest() {
        Role roleAdmin = roleRepository.findRoleAdmin();
        Role roleUser = roleRepository.findRoleStudent();
        Set<Role> roles;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            roles = new HashSet<>();
            User user = new User("lvat", "lvat0" + i, "lvat0" + i + "@gmail.com", "123456",
                    true, true, true, true, User.AuthProvider.LOCAL, null);

            switch (i % 2) {
                case 0:
                    roles.add(roleAdmin);
                case 1:
                    roles.add(roleUser);
                    break;
                default:
                    break;
            }
            System.out.println(roles.size());
            user.setRoles(roles);
            users.add(user);
        }
        try {
            return userRepository.saveAll(users);
        } catch (Exception e) {
            return null;
        }
    }

    public User findByCredentials(String usernameOrEmail, String password) throws UserNotFoundException {
        return userRepository.findByCredentials(usernameOrEmail, password).orElseThrow(UserNotFoundException::new);
    }

    public Boolean existsByCredentials(String usernameOrEmail, String password) {
        return userRepository.existsByCredentials(usernameOrEmail, password);
    }

    public User createNewUser(String name, String username, String email, String password) throws UserConflictException {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new UserConflictException();
        }
        User user = new User(name, username, email, password,
                null, null, null, null);
        Role roleUser = roleRepository.findRoleStudent();
        user.setRoles(Collections.singleton(roleUser));
        return userRepository.save(user);
    }

    public Boolean promoteUser(String publicUserId) {
        User user = userRepository.findByPublicUserId(publicUserId).orElse(null);
        if (user != null) {
            Role roleAdmin = roleRepository.findRoleAdmin();
            if (!user.getRoles().contains(roleAdmin)) {
                user.getRoles().add(roleAdmin);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean demoteUser(String publicUserId) {
        User user = userRepository.findByPublicUserId(publicUserId).orElse(null);
        if (user != null) {
            Role roleAdmin = roleRepository.findRoleAdmin();
            if (user.getRoles().contains(roleAdmin)) {
                user.getRoles().remove(roleAdmin);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

//    public void changePassword(String publicUserId, String oldPassword, String newPassword) {
//        userRepository.fin;
//    }
}
