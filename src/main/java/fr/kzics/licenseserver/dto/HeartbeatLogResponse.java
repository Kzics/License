package fr.kzics.licenseserver.dto;

import fr.kzics.licenseserver.entity.HeartbeatLog;

import java.time.Instant;

public record HeartbeatLogResponse(
        int playerCount,
        double tps,
        long usedMemoryMb,
        Instant recordedAt
) {
    public static HeartbeatLogResponse from(HeartbeatLog log) {
        return new HeartbeatLogResponse(
                log.getPlayerCount(),
                log.getTps(),
                log.getUsedMemoryMb(),
                log.getRecordedAt()
        );
    }
}
