package fr.kzics.licenseserver.dto;

import java.util.List;

public record StatsResponse(
        long totalServersOnline,
        long totalPlayersOnline,
        List<ProductStats> productBreakdown
) {
    public record ProductStats(
            String productId,
            long serverCount,
            long playerCount
    ) {}
}
