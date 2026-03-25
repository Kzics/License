package fr.kzics.licenseserver.dto;

import fr.kzics.licenseserver.entity.ServerInstance;

import java.time.Instant;

public record ServerResponse(
        String id,
        String serverIp,
        int serverPort,
        String motd,
        int playerCount,
        int maxPlayers,
        String mcVersion,
        String serverSoftware,
        String pluginVersion,
        double tps,
        long usedMemoryMb,
        long maxMemoryMb,
        boolean onlineMode,
        String javaVersion,
        String osName,
        long uptimeMs,
        String productId,
        String status,
        Instant firstSeen,
        Instant lastSeen
) {
    public static ServerResponse from(ServerInstance s) {
        return new ServerResponse(
                s.getId().toString(),
                s.getServerIp(),
                s.getServerPort(),
                s.getMotd(),
                s.getPlayerCount(),
                s.getMaxPlayers(),
                s.getMcVersion(),
                s.getServerSoftware(),
                s.getPluginVersion(),
                s.getTps(),
                s.getUsedMemoryMb(),
                s.getMaxMemoryMb(),
                s.isOnlineMode(),
                s.getJavaVersion(),
                s.getOsName(),
                s.getUptimeMs(),
                s.getProductId(),
                s.getStatus().name(),
                s.getFirstSeen(),
                s.getLastSeen()
        );
    }
}
