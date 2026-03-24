package fr.kzics.licenseserver.dto;

public record LoginResponse(
        String token,
        String username,
        long expiresIn
) {}
