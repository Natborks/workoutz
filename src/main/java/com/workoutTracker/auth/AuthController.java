package com.workoutTracker.auth;

import com.workoutTracker.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    String getToken(Authentication authentication) {
        LOG.debug("Token requested for user: {}", authentication.getName());
        String token = tokenService.generateToken(authentication);
        return token;
    }
}
