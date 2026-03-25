package fr.kzics.licenseserver.service;

import fr.kzics.licenseserver.dto.HeartbeatRequest;
import fr.kzics.licenseserver.dto.StatsResponse;
import fr.kzics.licenseserver.entity.HeartbeatLog;
import fr.kzics.licenseserver.entity.ServerInstance;
import fr.kzics.licenseserver.entity.ServerInstance.ServerStatus;
import fr.kzics.licenseserver.repository.HeartbeatLogRepository;
import fr.kzics.licenseserver.repository.ServerInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class HeartbeatService {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);

    private final ServerInstanceRepository serverRepo;
    private final HeartbeatLogRepository heartbeatLogRepo;

    public HeartbeatService(ServerInstanceRepository serverRepo,
                            HeartbeatLogRepository heartbeatLogRepo) {
        this.serverRepo = serverRepo;
        this.heartbeatLogRepo = heartbeatLogRepo;
    }

    /**
     * Traite un heartbeat entrant.
     * Identifie le serveur par (productId, serverIp, serverPort) — pas besoin de licence.
     */
    @Transactional
    public void processHeartbeat(HeartbeatRequest request, boolean isShutdown) {
        var serverOpt = serverRepo.findByProductIdAndServerIpAndServerPort(
                request.productId(), request.serverIp(), request.serverPort());

        ServerInstance server;
        if (serverOpt.isPresent()) {
            server = serverOpt.get();
        } else {
            server = ServerInstance.builder()
                    .serverIp(request.serverIp())
                    .serverPort(request.serverPort())
                    .productId(request.productId())
                    .firstSeen(Instant.now())
                    .build();
        }

        // Shutdown signal
        if (isShutdown || request.playerCount() < 0) {
            server.setStatus(ServerStatus.SHUTDOWN);
            server.setPlayerCount(0);
            server.setLastSeen(Instant.now());
            serverRepo.save(server);
            log.info("Serveur shutdown: {}:{} ({})", request.serverIp(), request.serverPort(), request.productId());
            return;
        }

        // Update metrics
        server.setMotd(request.motd());
        server.setPlayerCount(request.playerCount());
        server.setMaxPlayers(request.maxPlayers());
        server.setMcVersion(request.mcVersion());
        server.setServerSoftware(request.serverSoftware());
        server.setPluginVersion(request.pluginVersion());
        server.setTps(request.tps());
        server.setUsedMemoryMb(request.usedMemoryMb());
        server.setMaxMemoryMb(request.maxMemoryMb());
        server.setOnlineMode(request.onlineMode());
        server.setJavaVersion(request.javaVersion());
        server.setOsName(request.osName());
        server.setUptimeMs(request.uptimeMs());
        server.setStatus(ServerStatus.ONLINE);
        server.setLastSeen(Instant.now());

        serverRepo.save(server);

        // Log for graphs (sampled every heartbeat)
        var heartbeatLog = HeartbeatLog.builder()
                .server(server)
                .playerCount(request.playerCount())
                .tps(request.tps())
                .usedMemoryMb(request.usedMemoryMb())
                .recordedAt(Instant.now())
                .build();
        heartbeatLogRepo.save(heartbeatLog);
    }

    /**
     * Marque les serveurs qui n'ont pas envoye de heartbeat depuis le seuil comme OFFLINE.
     */
    @Transactional
    public int markStaleServers(int staleThresholdSeconds) {
        var threshold = Instant.now().minusSeconds(staleThresholdSeconds);
        return serverRepo.markStaleServers(ServerStatus.ONLINE, ServerStatus.OFFLINE, threshold);
    }

    /**
     * Supprime les logs de heartbeat plus vieux que la duree specifiee.
     */
    @Transactional
    public int cleanupOldLogs(int retentionDays) {
        var before = Instant.now().minusSeconds((long) retentionDays * 86400);
        return heartbeatLogRepo.deleteOlderThan(before);
    }

    /**
     * Recupere les statistiques globales (sans licences).
     */
    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        long totalOnline = serverRepo.countOnline();
        long totalPlayers = serverRepo.totalOnlinePlayers();

        // Breakdown par produit
        var productIds = serverRepo.findDistinctProductIds();
        var breakdown = new ArrayList<StatsResponse.ProductStats>();
        for (var productId : productIds) {
            var servers = serverRepo.findByProductId(productId).stream()
                    .filter(s -> s.getStatus() == ServerStatus.ONLINE)
                    .toList();
            long serverCount = servers.size();
            long playerCount = servers.stream().mapToLong(ServerInstance::getPlayerCount).sum();
            breakdown.add(new StatsResponse.ProductStats(productId, serverCount, playerCount));
        }

        return new StatsResponse(totalOnline, totalPlayers, breakdown);
    }

    @Transactional(readOnly = true)
    public List<ServerInstance> getAllOnlineServers() {
        return serverRepo.findAllOnlineWithLicense();
    }

    @Transactional(readOnly = true)
    public List<ServerInstance> getAllServers() {
        return serverRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<HeartbeatLog> getServerHistory(java.util.UUID serverId, int hoursBack) {
        var since = Instant.now().minusSeconds((long) hoursBack * 3600);
        return heartbeatLogRepo.findRecentByServerId(serverId, since);
    }
}
