package org.ms.apigateway.mspartnerships;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PartnerController {
    private final PartnerService partnerService;

    @PostMapping
    public ResponseEntity<Partner> createPartner(@RequestBody Partner partner) {
        return new ResponseEntity<>(partnerService.createPartner(partner), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Partner>> getAllPartners() {
        return ResponseEntity.ok(partnerService.getAllPartners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partner> getPartnerById(@PathVariable Long id) {
        return ResponseEntity.ok(partnerService.getPartnerById(id));
    }

    @PutMapping("/{id}")
    public Partner updatePartner(@PathVariable Long id, @RequestBody Partner partner) {
        return partnerService.updatePartner(id, partner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
    }

    //Advanced Functionality

    @GetMapping("/badge")
    public ResponseEntity<Map<String, Object>> generatePartnerBadge(
            @RequestParam Long partnerId,
            @RequestParam(defaultValue = "Exclusive partner offer") String offerDescription) {

        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            String badgeCode = generateBadgeCode(partner);

            Map<String, Object> response = new HashMap<>();
            response.put("partnerId", partner.getId());
            response.put("name", partner.getName());
            response.put("type", partner.getType() != null ? partner.getType() : "GENERAL");
            response.put("email", partner.getEmail());
            response.put("phone", partner.getPhone());
            response.put("badgeCode", badgeCode);
            response.put("offer", offerDescription);
            response.put("redeemUrl", buildRedeemUrl(badgeCode));
            response.put("qrCodeUrl", buildQrCodeUrl(badgeCode));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Partner with ID " + partnerId + " not found"
            );
        }
    }

    @GetMapping(value = "/badge-qr", produces = "text/html")
    public String generatePartnerBadgeQr(
            @RequestParam Long partnerId,
            @RequestParam(defaultValue = "Special partner offer") String offerDescription) {

        try {
            Partner partner = partnerService.getPartnerById(partnerId);
            String badgeCode = generateBadgeCode(partner);
            String qrCodeUrl = buildQrCodeUrl(badgeCode);

            return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>%s Partner Badge</title>
                    <style>
                        body { font-family: Arial, sans-serif; text-align: center; padding: 20px; }
                        h1 { color: #2c3e50; }
                        .badge-container { 
                            background: #f8f9fa; 
                            border-radius: 10px; 
                            padding: 20px; 
                            display: inline-block;
                            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                        }
                    </style>
                </head>
                <body>
                    <div class="badge-container">
                        <h1>%s</h1>
                        <h3>%s Partner</h3>
                        <img src="%s" alt="QR Code"/>
                        <p><strong>Offer:</strong> %s</p>
                        <p><strong>Code:</strong> %s</p>
                    </div>
                </body>
                </html>
                """,
                    partner.getName(),
                    partner.getName(),
                    partner.getType() != null ? partner.getType() : "General",
                    qrCodeUrl,
                    offerDescription,
                    badgeCode
            );
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Partner not found"
            );
        }
    }

    /* ========== NEW REDEEM ENDPOINT ========== */
    @GetMapping("/redeem")
    public ResponseEntity<String> redeemOffer(@RequestParam String code) {
        try {
            // Parse the badge code (format: TYPE_ID_YEAR)
            String[] parts = code.split("_");
            if (parts.length != 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid badge code format");
            }

            Long partnerId = Long.parseLong(parts[1]);
            Partner partner = partnerService.getPartnerById(partnerId);

            String redemptionHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Offer Redeemed</title>
                    <style>
                        body { font-family: Arial, sans-serif; text-align: center; padding: 40px; }
                        .success { 
                            background: #e8f5e9;
                            border-radius: 10px;
                            padding: 20px;
                            max-width: 500px;
                            margin: 0 auto;
                        }
                    </style>
                </head>
                <body>
                    <div class="success">
                        <h1>🎉 Offer Redeemed!</h1>
                        <h2>%s</h2>
                        <p>Code: <strong>%s</strong></p>
                        <p>Redeemed on: %s</p>
                    </div>
                </body>
                </html>
                """.formatted(
                    partner.getName(),
                    code,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            return ResponseEntity.ok(redemptionHtml);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid partner ID in code");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner not found");
        }
    }

    /* ========== HELPER METHODS ========== */
    private String generateBadgeCode(Partner partner) {
        String type = partner.getType() != null ?
                partner.getType().toUpperCase() :
                "GEN";
        return "%s_%d_%d"
                .formatted(
                        type,
                        partner.getId(),
                        LocalDate.now().getYear()
                );
    }

    private String buildRedeemUrl(String badgeCode) {
        return "http://localhost:8090/api/partners/redeem?code=" +
                URLEncoder.encode(badgeCode, StandardCharsets.UTF_8);
    }

    private String buildQrCodeUrl(String badgeCode) {
        return "https://api.qrserver.com/v1/create-qr-code/?" +
                "size=200x200&data=" +
                URLEncoder.encode(buildRedeemUrl(badgeCode), StandardCharsets.UTF_8);
    }
}