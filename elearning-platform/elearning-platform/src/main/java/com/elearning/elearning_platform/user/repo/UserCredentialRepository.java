package com.elearning.elearning_platform.user.repo;

import com.elearning.elearning_platform.user.domain.UserCredential;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
	
	@Query("""
	        SELECT uc
	        FROM UserCredential uc
	        JOIN uc.user u
	        WHERE LOWER(u.email) = LOWER(:email)
	    """)
	Optional<UserCredential> findByEmail(@Param("email") String email);
}
