package com.tripiz.api.wallet.service;

import com.tripiz.api.common.domain.User;
import com.tripiz.api.common.repository.UserRepository;
import com.tripiz.api.wallet.domain.PaymentMethod;
import com.tripiz.api.wallet.repositories.PaymentMethodRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// PaymentMethodService.java
@Service
@Transactional
public class PaymentMethodService {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    UserRepository userRepository;

    public PaymentMethod addPaymentMethod(UUID userId, PaymentMethodDTO dto) {
        User user = userRepository.findById(userId).orElseThrow();

        PaymentMethod pm = new PaymentMethod();
        pm.setUser(user);
        pm.setType(dto.getType());
        pm.setTokenizedData(encryptData(dto.getCardDetails())); // Chiffrement des données
        pm.setDefault(dto.isDefault());

        return paymentMethodRepository.save(pm);
    }

    private String encryptData(String data) {
        // Implémentation du chiffrement (AES ou via un service externe)
        return "encrypted:" + data;
    }

    public List<PaymentMethod> getUserPaymentMethods(Long userId) {
        return paymentMethodRepository.findByUserId(userId);
    }
}
