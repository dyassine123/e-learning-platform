package com.elearning.elearning_platform.user.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 180)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 500)
    private String bio;

    @Column(length = 500)
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {
        // JPA
    }

    public User(String fullName, String email, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public Instant getCreatedAt() { return createdAt; }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    public void rename(String newFullName) {
        this.fullName = newFullName;
    }

    public void updateProfile(String newFullName, String bio, String avatarUrl) {
        if (newFullName != null && !newFullName.isBlank()) {
            this.fullName = newFullName.trim();
        }
        this.bio = bio == null ? null : bio.trim();
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }
}
