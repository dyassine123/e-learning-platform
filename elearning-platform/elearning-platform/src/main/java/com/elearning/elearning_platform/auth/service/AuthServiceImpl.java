package com.elearning.elearning_platform.auth.service;

import com.elearning.elearning_platform.auth.dto.*;
import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.User;
import com.elearning.elearning_platform.user.domain.UserCredential;
import com.elearning.elearning_platform.user.repo.UserCredentialRepository;
import com.elearning.elearning_platform.user.repo.UserRepository;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
        UserRepository userRepository,
        UserCredentialRepository credentialRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already exists.");
        }

        User user = new User(request.fullName().trim(), email, request.role());
        User savedUser = userRepository.save(user);

        String hash = passwordEncoder.encode(request.password());
        UserCredential credential = new UserCredential(savedUser, hash);
        credentialRepository.save(credential);
    }

    @Override
    //@Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // load credential by email
        UserCredential cred = credentialRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Invalid email or password."));

        if (!cred.isEnabled() || cred.isLocked()) {
            throw new ConflictException("Account is disabled or locked.");
        }

        if (!passwordEncoder.matches(request.password(), cred.getPasswordHash())) {
            throw new NotFoundException("Invalid email or password.");
        }

        // issue token
        JwtService.TokenResult token = jwtService.issueToken(cred.getUser());

        // update last login (write transaction required)
        // easiest: switch method to read-write, or do a small write here:
        // (Since we used readOnly=true, remove that annotation if you want this)
        cred.setLastLoginAt(Instant.now());

        return new AuthResponse("Bearer", token.token(), token.expiresAt());
    }
}
