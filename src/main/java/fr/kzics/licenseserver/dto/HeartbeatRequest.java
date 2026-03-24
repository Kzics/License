package fr.kzics.licenseserver.dto;

import jakarta.validation.constraints.NotBlank;

public record HeartbeatRequest(
        @NotBlank String productId,
        @NotBlank String licenseKey,
        @NotBlank String serverIp,
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
        long uptimeMs
) {}
