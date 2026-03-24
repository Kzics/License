package fr.kzics.licenseserver.dto;

public record VerifyRequest(
        String key,       // Peut être vide/null pour les plugins gratuits
        String product,   // Toujours requis
        String server     // IP du serveur qui vérifie
) {
    // Normalisation : clé null → chaîne vide
    public String normalizedKey() {
        return key == null ? "" : key.trim();
    }

    public boolean hasKey() {
        return key != null && !key.isBlank();
    }
}
