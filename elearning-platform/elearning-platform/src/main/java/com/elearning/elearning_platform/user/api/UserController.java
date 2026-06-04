package com.elearning.elearning_platform.user.api;

import com.elearning.elearning_platform.shared.security.CurrentUser;
import com.elearning.elearning_platform.user.dto.CreateUserRequest;
import com.elearning.elearning_platform.user.dto.UpdateProfileRequest;
import com.elearning.elearning_platform.user.dto.UserResponse;
import com.elearning.elearning_platform.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list();
    }

    // --- Profile endpoints ---

    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication auth, 
                                     CurrentUser currentUser) {
        return userService.getById(currentUser.userId(auth));
    }

    @PutMapping("/me")
    public UserResponse updateProfile(Authentication auth, 
                                      CurrentUser currentUser,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(currentUser.userId(auth), request);
    }

    @PutMapping("/{id}/toggle-status")
    public UserResponse toggleStatus(@PathVariable Long id) {
        return userService.toggleStatus(id);
    }
}
