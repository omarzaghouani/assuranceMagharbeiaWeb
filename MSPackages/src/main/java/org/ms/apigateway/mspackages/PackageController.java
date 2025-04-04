package org.ms.apigateway.mspackages;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    public List<Package> getAllPackages() {
        return packageService.getAllPackages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable int id) {
        return ResponseEntity.of(packageService.getPackageById(id));
    }

    @PostMapping
    public Package createPackage(@RequestBody Package p) {
        return packageService.savePackage(p);
    }

    @DeleteMapping("/{id}")
    public void deletePackage(@PathVariable int id) {
        packageService.deletePackage(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Package> updatePackage(@PathVariable int id, @RequestBody Package updatedPackage) {
        Package result = packageService.updatePackage(id, updatedPackage);
        return ResponseEntity.ok(result);
    }

    //Advanced Functions

    @GetMapping("/recommend")
    public ResponseEntity<List<Package>> recommendPackages(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "3") int limit) {

        List<Package> allPackages = packageService.getAllPackages();

        // 1. Filter by category if specified
        List<Package> filtered = (category != null) ?
                allPackages.stream()
                        .filter(p -> p.getCategory() != null)
                        .filter(p -> p.getCategory().equalsIgnoreCase(category))
                        .collect(Collectors.toList()) :
                allPackages;

        // 2. Recommendation logic (customize these rules)
        List<Package> recommended = filtered.stream()
                .sorted(Comparator
                        .comparingInt((Package p) -> p.getPopularityScore() != null ? p.getPopularityScore() : 0).reversed()
                        .thenComparingDouble(p -> p.getDiscount() != null ? p.getDiscount() : 0).reversed()
                )
                .limit(limit)
                .collect(Collectors.toList());

        if (recommended.isEmpty()) {
            throw new ResponseStatusException(
                    NOT_FOUND,
                    "No packages found" + (category != null ? " in category: " + category : "")
            );
        }

        return ResponseEntity.ok(recommended);
    }

    @GetMapping("/combo")
    public ResponseEntity<Map<String, Object>> recommendCombo(
            @RequestParam int basePackageId) {

        Package basePackage = packageService.getPackageById(basePackageId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Base package not found"));

        // 1. Find complementary packages (using != for primitive int comparison)
        List<Package> combos = packageService.getAllPackages().stream()
                .filter(p -> p.getId() != basePackageId) // Changed to != for primitive int
                .filter(p -> p.getCategory() != null && basePackage.getCategory() != null)
                .filter(p -> p.getCategory().equals(basePackage.getCategory()))
                .sorted(Comparator.comparingDouble(Package::getDiscount).reversed())
                .limit(2)
                .collect(Collectors.toList());

        // 2. Calculate combo discount (with null safety)
        double totalDiscount = Optional.ofNullable(basePackage.getDiscount()).orElse(0.0) +
                combos.stream()
                        .mapToDouble(p -> Optional.ofNullable(p.getDiscount()).orElse(0.0))
                        .sum();

        // 3. Build response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("basePackage", basePackage);
        response.put("recommendedCombos", combos);
        response.put("totalDiscount", String.format("%.1f%%", totalDiscount));
        response.put("description", "Recommended bundle for " +
                Optional.ofNullable(basePackage.getCategory()).orElse("all categories"));

        return ResponseEntity.ok(response);
    }
}
