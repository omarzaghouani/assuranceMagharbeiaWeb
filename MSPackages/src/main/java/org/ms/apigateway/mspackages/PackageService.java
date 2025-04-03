package org.ms.apigateway.mspackages;
import org.ms.apigateway.mspackages.Package;
import org.ms.apigateway.mspackages.PackageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class PackageService {
    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Optional<Package> getPackageById(int id) {
        return packageRepository.findById(id);
    }


    public Package savePackage(Package p) {
        return packageRepository.save(p);
    }

    public void deletePackage(int id) {
        packageRepository.deleteById(id);
    }

    public Package updatePackage(int id, Package updatedPackage) {
        return packageRepository.findById(id)
                .map(existingPackage -> {
                    // Update only non-null fields
                    if (updatedPackage.getName() != null) {
                        existingPackage.setName(updatedPackage.getName());
                    }
                    if (updatedPackage.getDescription() != null) {
                        existingPackage.setDescription(updatedPackage.getDescription());
                    }
                    if (updatedPackage.getPrice() != null) {
                        existingPackage.setPrice(updatedPackage.getPrice());
                    }
                    if (updatedPackage.getInsuranceType() != null) {
                        existingPackage.setInsuranceType(updatedPackage.getInsuranceType());
                    }
                    if (updatedPackage.getDuration() != 0) {
                        existingPackage.setDuration(updatedPackage.getDuration());
                    }
                    return packageRepository.save(existingPackage);
                })
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
    }
}
