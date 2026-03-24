package fr.kzics.licenseserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "server_instances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"license_id", "server_ip", "server_port"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ServerInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @Column(name = "server_ip", nullable = false, length = 128)
    private String serverIp;

    @Column(name = "server_port", nullable = false)
    @Builder.Default
    private int serverPort = 25565;

    @Column(length = 255)
    private String motd;

    @Column(name = "player_count")
    @Builder.Default
    private int playerCount = 0;

    @Column(name = "max_players")
    @Builder.Default
    private int maxPlayers = 0;

    @Column(name = "mc_version", length = 32)
    private String mcVersion;

    @Column(name = "server_software", length = 64)
    private String serverSoftware;

    @Column(name = "plugin_version", length = 32)
    private String pluginVersion;

    @Column
    @Builder.Default
    private double tps = 20.0;

    @Column(name = "used_memory_mb")
    @Builder.Default
    private long usedMemoryMb = 0;

    @Column(name = "max_memory_mb")
    @Builder.Default
    private long maxMemoryMb = 0;

    @Column(name = "online_mode")
    @Builder.Default
    private boolean onlineMode = true;

    @Column(name = "java_version", length = 32)
    private String javaVersion;

    @Column(name = "os_name", length = 64)
    private String osName;

    @Column(name = "uptime_ms")
    @Builder.Default
    private long uptimeMs = 0;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private ServerStatus status = ServerStatus.ONLINE;

    @Column(name = "first_seen", nullable = false, updatable = false)
    @Builder.Default
    private Instant firstSeen = Instant.now();

    @Column(name = "last_seen", nullable = false)
    @Builder.Default
    private Instant lastSeen = Instant.now();

    public enum ServerStatus {
        ONLINE, OFFLINE, SHUTDOWN
    }
}
