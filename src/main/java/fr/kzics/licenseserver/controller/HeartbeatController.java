package fr.kzics.licenseserver.controller;

import fr.kzics.licenseserver.dto.HeartbeatRequest;
import fr.kzics.licenseserver.service.HeartbeatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HeartbeatController {

    private final HeartbeatService heartbeatService;

    public HeartbeatController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @Valid @RequestBody HeartbeatRequest request,
            @RequestHeader(value = "X-Shutdown", defaultValue = "false") boolean isShutdown
    ) {
        heartbeatService.processHeartbeat(request, isShutdown);
        return ResponseEntity.noContent().build();
    }
}
