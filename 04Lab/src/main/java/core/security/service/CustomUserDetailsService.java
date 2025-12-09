package core.security.service;
import core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import core.entity.*;
import core.entity.UserEntity;
import core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для загрузки пользователей по имени пользователя.
 * Реализует UserDetailsService Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("===  LOGIN ATTEMPT ===");
        System.out.println("Username from request: '" + username + "'");

        Optional<UserEntity> userOpt = userRepository.findByName(username);

        if (userOpt.isEmpty()) {
            System.out.println("User NOT found in database");
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserEntity user = userOpt.get();
        System.out.println("User found in DB: '" + user.getName() + "'");

        String storedPasswordHash = new String(user.getPassword(), StandardCharsets.UTF_8);
        System.out.println("   Password hash (first 30 chars): " +
                storedPasswordHash.substring(0, Math.min(30, storedPasswordHash.length())) + "...");

        return new User(
                user.getName(),
                storedPasswordHash,
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                Boolean.TRUE.equals(user.getIsAdmin())
                                        ? "ROLE_ADMIN"
                                        : "ROLE_USER"
                        )
                )
        );
    }
}