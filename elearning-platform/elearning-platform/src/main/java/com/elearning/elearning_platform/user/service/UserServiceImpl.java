package com.elearning.elearning_platform.user.service;

import com.elearning.elearning_platform.shared.error.ConflictException;
import com.elearning.elearning_platform.shared.error.NotFoundException;
import com.elearning.elearning_platform.user.domain.User;
import com.elearning.elearning_platform.user.domain.UserCredential;
import com.elearning.elearning_platform.user.dto.CreateUserRequest;
import com.elearning.elearning_platform.user.dto.UpdateProfileRequest;
import com.elearning.elearning_platform.user.dto.UserResponse;
import com.elearning.elearning_platform.user.mapper.UserMapper;
import com.elearning.elearning_platform.user.repo.UserCredentialRepository;
import com.elearning.elearning_platform.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;

    public UserServiceImpl(UserRepository userRepository, UserCredentialRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        final String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already exists.");
        }

        User user = new User(request.fullName().trim(), email, request.role());
        User saved = userRepository.save(user);

        // Assume we don't have password here, but we need to create credentials
        // Usually creation of credentials happens in a separate flow or we set a default
        return UserMapper.toResponse(saved, true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found."));
        boolean enabled = credentialRepository.findById(id)
            .map(UserCredential::isEnabled)
            .orElse(true);
        return UserMapper.toResponse(user, enabled);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream()
            .map(user -> {
                boolean enabled = credentialRepository.findById(user.getId())
                    .map(UserCredential::isEnabled)
                    .orElse(true);
                return UserMapper.toResponse(user, enabled);
            })
            .toList();
    }

    @Override
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found."));

        user.updateProfile(request.fullName(), request.bio(), request.avatarUrl());
        boolean enabled = credentialRepository.findById(userId)
            .map(UserCredential::isEnabled)
            .orElse(true);
        return UserMapper.toResponse(user, enabled);
    }

    @Override
    public UserResponse toggleStatus(Long id) {
        UserCredential cred = credentialRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User credentials not found."));
        
        cred.setEnabled(!cred.isEnabled());
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found."));
        
        return UserMapper.toResponse(user, cred.isEnabled());
    }
}
