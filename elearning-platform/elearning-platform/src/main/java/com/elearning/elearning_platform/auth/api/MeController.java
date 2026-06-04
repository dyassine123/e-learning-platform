package com.elearning.elearning_platform.auth.api;

import com.elearning.elearning_platform.auth.dto.MeResponse;
import com.elearning.elearning_platform.user.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public MeResponse me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Long uid = jwt.getClaim("uid");
        String email = jwt.getSubject();
        Role role = Role.valueOf(jwt.getClaimAsString("role"));

        return new MeResponse(uid, email, role);
    }
}
