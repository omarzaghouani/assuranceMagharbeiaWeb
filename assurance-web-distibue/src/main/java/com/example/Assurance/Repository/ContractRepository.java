package com.example.Assurance.Repository;

import com.example.Assurance.Entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByUser_IdUser(Long userId);
    List<Contract> findByUser_IdUserAndActive(Long userId, boolean active);
    List<Contract> findAll();
} 