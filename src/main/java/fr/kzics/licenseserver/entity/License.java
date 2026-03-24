package fr.kzics.licenseserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "licenses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "license_key", nullable = false, unique = true, length = 64)
    private String licenseKey;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(nullable = false, length = 128)
    private String owner;

    @Column(length = 255)
    private String email;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "max_servers")
    @Builder.Default
    private int maxServers = -1; // -1 = illimite

    /**
     * Si true, le produit est gratuit — la clé n'est pas vérifiée.
     * C'est le back-end (cette colonne en DB) qui décide, pas le client.
     */
    @Column(name = "free", nullable = false)
    @Builder.Default
    private boolean free = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServerInstance> servers = new ArrayList<>();

    public boolean isExpired() {
        return expiryDate != null && Instant.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return active && !isExpired();
    }
}
