package com.elearning.elearning_platform.shared.seed;

import com.elearning.elearning_platform.user.domain.Role;
import com.elearning.elearning_platform.user.domain.User;
import com.elearning.elearning_platform.user.domain.UserCredential;
import com.elearning.elearning_platform.user.repo.UserCredentialRepository;
import com.elearning.elearning_platform.user.repo.UserRepository;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class AdminSeeder {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       UserCredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    ApplicationRunner seedAdminRunner() {
        return args -> seedAdminIfMissing();
    }

    @Transactional
    void seedAdminIfMissing() {
        String email = "admin@elearning.com";

        if (userRepository.existsByEmailIgnoreCase(email)) return;

        User admin = new User("System Administrator", email, Role.ADMIN);
        userRepository.save(admin);

        UserCredential cred = new UserCredential(admin, passwordEncoder.encode("Admin123!"));
        credentialRepository.save(cred);
    }
}