package com.elearning.elearning_platform.auth.service;

import com.elearning.elearning_platform.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlSeconds;

    public JwtServiceImpl(
        JwtEncoder jwtEncoder,
        @Value("${security.jwt.issuer}") String issuer,
        @Value("${security.jwt.ttl-seconds}") long ttlSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public TokenResult issueToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(exp)
            .subject(user.getEmail())
            .claim("uid", user.getId())
            .claim("role", user.getRole().name())
            .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var params = JwtEncoderParameters.from(header, claims);

        String token = jwtEncoder.encode(params).getTokenValue();
        return new TokenResult(token, exp);
    }
}
