package org.ms.apigateway.mspartnerships;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartnerService {
    private final PartnerRepository partnerRepository;

    public Partner createPartner(Partner partner) {
        if (partnerRepository.existsByEmail(partner.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return partnerRepository.save(partner);
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Partner getPartnerById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }
}