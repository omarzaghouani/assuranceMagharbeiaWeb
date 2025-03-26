package org.ms.apigateway.mspackages;

import org.ms.apigateway.mspackages.Package;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PackageRepository extends JpaRepository<Package, Integer>{
}
