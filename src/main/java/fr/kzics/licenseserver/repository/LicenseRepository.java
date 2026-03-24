package fr.kzics.licenseserver.repository;

import fr.kzics.licenseserver.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LicenseRepository extends JpaRepository<License, UUID> {

    Optional<License> findByLicenseKeyAndProductId(String licenseKey, String productId);

    Optional<License> findByLicenseKey(String licenseKey);

    /** Trouve l'entrée "gratuit" pour un produit (pas besoin de clé). */
    Optional<License> findFirstByProductIdAndFreeTrue(String productId);

    long countByProductId(String productId);

    long countByActive(boolean active);
}
