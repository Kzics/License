package fr.kzics.licenseserver.repository;

import fr.kzics.licenseserver.entity.HeartbeatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface HeartbeatLogRepository extends JpaRepository<HeartbeatLog, UUID> {

    List<HeartbeatLog> findByServerIdOrderByRecordedAtDesc(UUID serverId);

    @Query("SELECT h FROM HeartbeatLog h WHERE h.server.id = :serverId AND h.recordedAt > :since ORDER BY h.recordedAt ASC")
    List<HeartbeatLog> findRecentByServerId(@Param("serverId") UUID serverId, @Param("since") Instant since);

    @Modifying
    @Query("DELETE FROM HeartbeatLog h WHERE h.recordedAt < :before")
    int deleteOlderThan(@Param("before") Instant before);
}
