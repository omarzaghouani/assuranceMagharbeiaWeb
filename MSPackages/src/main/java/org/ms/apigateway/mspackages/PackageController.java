package org.ms.apigateway.mspackages;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
