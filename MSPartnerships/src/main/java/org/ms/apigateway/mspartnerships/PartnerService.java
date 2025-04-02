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

    public Partner updatePartner(Long id, Partner partnerDetails) {
        Partner existingPartner = partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with id: " + id));

        // Update only non-null fields from partnerDetails
        if (partnerDetails.getName() != null) {
            existingPartner.setName(partnerDetails.getName());
        }
        if (partnerDetails.getType() != null) {
            existingPartner.setType(partnerDetails.getType());
        }
        if (partnerDetails.getEmail() != null) {
            existingPartner.setEmail(partnerDetails.getEmail());
        }
        if (partnerDetails.getPhone() != null) {
            existingPartner.setPhone(partnerDetails.getPhone());
        }

        return partnerRepository.save(existingPartner);
    }

    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }
}