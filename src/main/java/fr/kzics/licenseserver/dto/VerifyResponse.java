package fr.kzics.licenseserver.dto;

import java.time.Instant;

/**
 * Réponse du serveur de licence.
 *
 * Le champ {@code status} est lu par le client (license-lib) :
 * <ul>
 *   <li>{@code "free"}    → plugin gratuit, aucune clé requise</li>
 *   <li>{@code "valid"}   → clé valide, plugin payant</li>
 *   <li>{@code "invalid"} → clé invalide</li>
 *   <li>{@code "expired"} → clé expirée</li>
 * </ul>
 */
public record VerifyResponse(
        String status,   // "free" | "valid" | "invalid" | "expired" — lu par le client
        boolean valid,   // Raccourci booléen
        String owner,
        String expiry,
        String message
) {
    public static VerifyResponse free() {
        return new VerifyResponse("free", true, null, "never", "Free plugin — no license key required.");
    }

    public static VerifyResponse valid(String owner, Instant expiry) {
        return new VerifyResponse("valid", true, owner, expiry != null ? expiry.toString() : "never", "License valid.");
    }

    public static VerifyResponse invalid(String message) {
        return new VerifyResponse("invalid", false, null, null, message);
    }

    public static VerifyResponse expired(String owner, Instant expiry) {
        return new VerifyResponse("expired", false, owner, expiry != null ? expiry.toString() : null, "License expired.");
    }
}
