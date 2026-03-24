package fr.kzics.licenseserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Historique des heartbeats pour les graphiques (joueurs, TPS, RAM dans le temps).
 */
@Entity
@Table(name = "heartbeat_logs", indexes = {
        @Index(name = "idx_hb_server_time", columnList = "server_id, recorded_at DESC")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HeartbeatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerInstance server;

    @Column(name = "player_count")
    private int playerCount;

    @Column
    private double tps;

    @Column(name = "used_memory_mb")
    private long usedMemoryMb;

    @Column(name = "recorded_at", nullable = false)
    @Builder.Default
    private Instant recordedAt = Instant.now();
}
