package fr.kzics.licenseserver.controller;

import fr.kzics.licenseserver.dto.HeartbeatLogResponse;
import fr.kzics.licenseserver.dto.ServerResponse;
import fr.kzics.licenseserver.dto.StatsResponse;
import fr.kzics.licenseserver.service.HeartbeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final HeartbeatService heartbeatService;

    public DashboardController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    /**
     * Stats globales (total serveurs, joueurs, licences).
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats() {
        return ResponseEntity.ok(heartbeatService.getStats());
    }

    /**
     * Liste tous les serveurs (online + offline + shutdown).
     */
    @GetMapping("/servers")
    public ResponseEntity<List<ServerResponse>> servers(
            @RequestParam(value = "status", required = false) String status
    ) {
        var servers = heartbeatService.getAllServers();
        var response = servers.stream()
                .filter(s -> status == null || s.getStatus().name().equalsIgnoreCase(status))
                .map(ServerResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Detail d'un serveur + son historique de heartbeats.
     */
    @GetMapping("/servers/{id}")
    public ResponseEntity<ServerDetailResponse> serverDetail(
            @PathVariable UUID id,
            @RequestParam(value = "hours", defaultValue = "24") int hours
    ) {
        var servers = heartbeatService.getAllServers();
        var serverOpt = servers.stream().filter(s -> s.getId().equals(id)).findFirst();

        if (serverOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var server = serverOpt.get();
        var history = heartbeatService.getServerHistory(id, hours);
        var historyResponse = history.stream().map(HeartbeatLogResponse::from).toList();

        return ResponseEntity.ok(new ServerDetailResponse(
                ServerResponse.from(server),
                historyResponse
        ));
    }

    public record ServerDetailResponse(
            ServerResponse server,
            List<HeartbeatLogResponse> history
    ) {}
}
