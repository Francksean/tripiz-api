package com.tripiz.api.wallet.service;

import com.tripiz.api.wallet.domain.Beneficiary;
import com.tripiz.api.wallet.repositories.BeneficiaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BeneficiaryService {

    @Autowired
    BeneficiaryRepository beneficiaryRepository;

    public Beneficiary addBeneficiary(Long userId, BeneficiaryDTO dto) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(userId);
        beneficiary.setName(dto.getName());
        beneficiary.setAccountReference(dto.getAccountReference());
        beneficiary.setInstitution(dto.getInstitution());

        return beneficiaryRepository.save(beneficiary);
    }

    public List<Beneficiary> getUserBeneficiaries(Long userId) {
        return beneficiaryRepository.findByUserId(userId);
    }
}
