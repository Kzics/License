package fr.kzics.licenseserver.service;

import fr.kzics.licenseserver.dto.VerifyRequest;
import fr.kzics.licenseserver.dto.VerifyResponse;
import fr.kzics.licenseserver.repository.LicenseRepository;
import org.springframework.stereotype.Service;

@Service
public class LicenseService {

    private final LicenseRepository licenseRepository;

    public LicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    public VerifyResponse verify(VerifyRequest request) {
        if (request.product() == null || request.product().isBlank()) {
            return VerifyResponse.invalid("Product ID is required.");
        }

        // ── Étape 1 : le produit est-il gratuit en DB ? ─────────────────────
        // C'est ICI que la décision "gratuit" est prise, côté serveur.
        // Aucun client ne peut forcer ce comportement en décompilant son JAR.
        var freeEntry = licenseRepository.findFirstByProductIdAndFreeTrue(request.product());
        if (freeEntry.isPresent() && freeEntry.get().isActive()) {
            return VerifyResponse.free();
        }

        // ── Étape 2 : produit payant — clé obligatoire ───────────────────────
        if (!request.hasKey()) {
            return VerifyResponse.invalid("License key is required for this product.");
        }

        var licenseOpt = licenseRepository.findByLicenseKeyAndProductId(
                request.normalizedKey(), request.product());

        if (licenseOpt.isEmpty()) {
            return VerifyResponse.invalid("License key not found for this product.");
        }

        var license = licenseOpt.get();

        if (!license.isActive()) {
            return VerifyResponse.invalid("License has been deactivated.");
        }

        if (license.isExpired()) {
            return VerifyResponse.expired(license.getOwner(), license.getExpiryDate());
        }

        return VerifyResponse.valid(license.getOwner(), license.getExpiryDate());
    }
}
