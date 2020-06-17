package lvat.protest.jwta01.security;

import lvat.protest.jwta01.entity.User;
import lvat.protest.jwta01.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(s, s);
        return UserPrincipal.create(user.orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + s)));
    }

    public UserDetails loadUserByPublicUserId(String publicUserId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByPublicUserId(publicUserId);
        return UserPrincipal.create(user.orElseThrow(() -> new UsernameNotFoundException("User not found with public user id: " + publicUserId)));
    }
}
