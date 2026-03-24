package fr.kzics.licenseserver.scheduler;

import fr.kzics.licenseserver.service.HeartbeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StaleServerCleanup {

    private static final Logger log = LoggerFactory.getLogger(StaleServerCleanup.class);

    private final HeartbeatService heartbeatService;
    private final int staleThresholdSeconds;

    public StaleServerCleanup(
            HeartbeatService heartbeatService,
            @Value("${app.heartbeat.stale-threshold-seconds}") int staleThresholdSeconds
    ) {
        this.heartbeatService = heartbeatService;
        this.staleThresholdSeconds = staleThresholdSeconds;
    }

    /**
     * Toutes les 60 secondes, marque les serveurs sans heartbeat comme OFFLINE.
     */
    @Scheduled(fixedDelayString = "${app.heartbeat.cleanup-interval-seconds}000")
    public void cleanupStaleServers() {
        int marked = heartbeatService.markStaleServers(staleThresholdSeconds);
        if (marked > 0) {
            log.info("Marked {} stale server(s) as OFFLINE", marked);
        }
    }

    /**
     * Toutes les 6 heures, supprime les logs de heartbeat de plus de 7 jours.
     */
    @Scheduled(fixedDelay = 21600000) // 6h
    public void cleanupOldLogs() {
        int deleted = heartbeatService.cleanupOldLogs(7);
        if (deleted > 0) {
            log.info("Cleaned up {} old heartbeat log(s)", deleted);
        }
    }
}
