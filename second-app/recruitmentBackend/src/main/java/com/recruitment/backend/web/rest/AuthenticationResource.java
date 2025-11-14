package com.recruitment.backend.web.rest;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.recruitment.backend.security.SecurityUtils;
import com.recruitment.backend.web.rest.vm.JWTToken;
import com.recruitment.backend.web.rest.vm.LoginVM;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * REST controller for managing authentication checks and issuing local JWTs.
 */
@RestController
@RequestMapping("/api")
public class AuthenticationResource {

    private final Logger log = LoggerFactory.getLogger(AuthenticationResource.class);

    private final AuthenticationConfiguration authenticationConfiguration;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String base64Secret;

    public AuthenticationResource(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * {@code GET  /authenticate} : check if the authentication token correctly validates
     */
    @GetMapping("/authenticate")
    public ResponseEntity<Void> isAuthenticated() {
        log.debug("REST request to check if the current user is authenticated");
        if (SecurityUtils.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * {@code POST  /authenticate} : authenticate user and return a signed JWT using local JwtEncoder.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletResponse response) {
        log.debug("REST request to authenticate user : {}", loginVM.getUsername());
        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Instant now = Instant.now();
            long expiresIn = loginVM.isRememberMe() ? 604800L : 3600L; // 7 days vs 1 hour

            String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

            // build performed with Nimbus below

            // Try manual signing using Nimbus to avoid JWK selection issues
            JWTClaimsSet nimbusClaims = new JWTClaimsSet.Builder()
                .subject(authentication.getName())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(expiresIn)))
                .claim(SecurityUtils.AUTHORITIES_CLAIM, authorities)
                .build();

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            SignedJWT signedJWT = new SignedJWT(header, nimbusClaims);
            byte[] secret = Base64.getDecoder().decode(base64Secret);
            MACSigner signer = new MACSigner(secret);
            signedJWT.sign(signer);
            String token = signedJWT.serialize();

            // expose token in header and body
            response.setHeader("Authorization", "Bearer " + token);
            return ResponseEntity.ok(new JWTToken(token));
        } catch (Exception e) {
            // Log full stacktrace for debugging authentication failures in dev
            log.error("Authentication failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
