package com.example.Assurance.Service;

import com.example.Assurance.Entity.Contract;
import com.example.Assurance.Repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    public Contract createContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public Optional<Contract> getContract(Long id) {
        return contractRepository.findById(id);
    }

    public List<Contract> getUserContracts(Long userId) {
        return contractRepository.findByUser_IdUser(userId);
    }

    public List<Contract> getUserActiveContracts(Long userId) {
        return contractRepository.findByUser_IdUserAndActive(userId, true);
    }

    public Contract updateContract(Long id, Contract contractDetails) {
        Optional<Contract> contract = contractRepository.findById(id);
        if (contract.isPresent()) {
            Contract existingContract = contract.get();
            existingContract.setType(contractDetails.getType());
            existingContract.setStartDate(contractDetails.getStartDate());
            existingContract.setEndDate(contractDetails.getEndDate());
            existingContract.setActive(contractDetails.isActive());
            existingContract.setContractDetails(contractDetails.getContractDetails());
            return contractRepository.save(existingContract);
        }
        return null;
    }

    public boolean deleteContract(Long id) {
        Optional<Contract> contract = contractRepository.findById(id);
        if (contract.isPresent()) {
            contractRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }
} 