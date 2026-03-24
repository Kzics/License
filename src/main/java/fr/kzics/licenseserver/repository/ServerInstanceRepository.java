package fr.kzics.licenseserver.repository;

import fr.kzics.licenseserver.entity.ServerInstance;
import fr.kzics.licenseserver.entity.ServerInstance.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServerInstanceRepository extends JpaRepository<ServerInstance, UUID> {

    Optional<ServerInstance> findByLicenseIdAndServerIpAndServerPort(UUID licenseId, String serverIp, int serverPort);

    List<ServerInstance> findByStatus(ServerStatus status);

    List<ServerInstance> findByProductId(String productId);

    @Query("SELECT s FROM ServerInstance s WHERE s.status = 'ONLINE'")
    List<ServerInstance> findAllOnline();

    @Query("SELECT s FROM ServerInstance s JOIN FETCH s.license WHERE s.status = 'ONLINE'")
    List<ServerInstance> findAllOnlineWithLicense();

    @Modifying
    @Query("UPDATE ServerInstance s SET s.status = :newStatus WHERE s.status = :currentStatus AND s.lastSeen < :threshold")
    int markStaleServers(@Param("currentStatus") ServerStatus currentStatus,
                         @Param("newStatus") ServerStatus newStatus,
                         @Param("threshold") Instant threshold);

    @Query("SELECT COUNT(s) FROM ServerInstance s WHERE s.status = 'ONLINE'")
    long countOnline();

    @Query("SELECT COALESCE(SUM(s.playerCount), 0) FROM ServerInstance s WHERE s.status = 'ONLINE'")
    long totalOnlinePlayers();

    @Query("SELECT DISTINCT s.productId FROM ServerInstance s WHERE s.status = 'ONLINE'")
    List<String> findDistinctProductIds();
}
