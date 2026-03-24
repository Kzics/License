package fr.kzics.licenseserver.controller;

import fr.kzics.licenseserver.dto.VerifyRequest;
import fr.kzics.licenseserver.dto.VerifyResponse;
import fr.kzics.licenseserver.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody VerifyRequest request) {
        var response = licenseService.verify(request);
        return switch (response.status()) {
            case "free", "valid" -> ResponseEntity.ok(response);
            case "expired"       -> ResponseEntity.status(402).body(response);
            default              -> ResponseEntity.status(403).body(response);
        };
    }
}
