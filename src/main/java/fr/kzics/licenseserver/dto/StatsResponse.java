package fr.kzics.licenseserver.dto;

import java.util.List;
import java.util.Map;

public record StatsResponse(
        long totalServersOnline,
        long totalPlayersOnline,
        long totalLicenses,
        long activeLicenses,
        List<ProductStats> productBreakdown
) {
    public record ProductStats(
            String productId,
            long serverCount,
            long playerCount
    ) {}
}
