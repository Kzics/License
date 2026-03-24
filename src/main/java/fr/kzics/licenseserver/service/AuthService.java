package fr.kzics.licenseserver.service;

import fr.kzics.licenseserver.dto.LoginRequest;
import fr.kzics.licenseserver.dto.LoginResponse;
import fr.kzics.licenseserver.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtTokenProvider tokenProvider;
    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
            JwtTokenProvider tokenProvider,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword
    ) {
        this.tokenProvider = tokenProvider;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public LoginResponse login(LoginRequest request) {
        if (!adminUsername.equals(request.username()) || !adminPassword.equals(request.password())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        var token = tokenProvider.generateToken(request.username());
        return new LoginResponse(token, request.username(), tokenProvider.getExpirationMs());
    }
}
