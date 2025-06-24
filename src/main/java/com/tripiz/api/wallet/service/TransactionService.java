package com.tripiz.api.wallet.service;

import com.tripiz.api.wallet.domain.*;
import com.tripiz.api.wallet.enums.TransactionStatus;
import com.tripiz.api.wallet.exceptions.InsufficientFundsException;
import com.tripiz.api.wallet.repositories.BalanceHistoryRepository;
import com.tripiz.api.wallet.repositories.PaymentMethodRepository;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    BalanceHistoryRepository balanceHistoryRepository;

    public Recharge initiateRecharge(RechargeRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow();

        // Vérifier les limites
//        checkTransactionLimits(wallet, request.getAmount(), LimitType.RECHARGE);

        Recharge recharge = new Recharge();
        recharge.setAmount(request.getAmount());
        recharge.setWallet(wallet);
        recharge.setTimestamp(LocalDateTime.now());
        recharge.setReference(UUID.randomUUID().toString());
        recharge.setStatus(TransactionStatus.PENDING);

        if(request.getPaymentMethodId() != null) {
            PaymentMethod pm = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow();
            recharge.setPaymentMethod(pm);
        }

        return transactionRepository.save(recharge);
    }

    public Payment initiatePayment(PaymentRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow();

        // Vérifier le solde et les limites
        if(wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Solde insuffisant");
        }
        checkTransactionLimits(wallet, request.getAmount(), LimitType.PAYMENT);

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setWallet(wallet);
        payment.setTimestamp(LocalDateTime.now());
        payment.setReference(UUID.randomUUID().toString());
        payment.setStatus(TransactionStatus.PENDING);
        payment.setOrderReference(request.getOrderReference());

        if(request.getBeneficiaryId() != null) {
            Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                    .orElseThrow();
            payment.setBeneficiary(beneficiary);
        }

        return transactionRepository.save(payment);
    }

//    private void checkTransactionLimits(Wallet wallet, BigDecimal amount, LimitType type) {
//        TransactionLimit limit = limitRepository.findByWalletIdAndLimitType(wallet.getId(), type)
//                .orElseThrow();
//
//        if(amount.compareTo(limit.getPerTransactionLimit()) > 0) {
//            throw new TransactionLimitExceededException("Limite par transaction dépassée");
//        }
//
//        // Vérifier la limite journalière (implémentation simplifiée)
//        BigDecimal dailyTotal = transactionRepository.calculateDailyTotal(wallet.getId(), type, LocalDate.now());
//        if(dailyTotal.add(amount).compareTo(limit.getDailyLimit()) > 0) {
//            throw new TransactionLimitExceededException("Limite journalière dépassée");
//        }
//    }

    public void completeTransaction(Long transactionId, boolean success) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow();

        if(transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction déjà traitée");
        }

        Wallet wallet = transaction.getWallet();
        double oldBalance = wallet.getBalance();

        if(success) {
            transaction.setStatus(TransactionStatus.SUCCESS);

            if(transaction instanceof Recharge) {
                wallet.setBalance(oldBalance += transaction.getAmount());
            } else if(transaction instanceof Payment) {
                wallet.setBalance(oldBalance -= transaction.getAmount());
            }

            // Audit du solde
            saveBalanceHistory(wallet, oldBalance, transaction.getClass().getSimpleName());
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }

    private void saveBalanceHistory(Wallet wallet, double oldBalance, String reason) {
        BalanceHistory history = new BalanceHistory();
        history.setWallet(wallet);
        history.setOldBalance(oldBalance);
        history.setNewBalance(wallet.getBalance());
        history.setChangeDate(LocalDateTime.now());
        history.setChangeReason(reason);
        balanceHistoryRepository.save(history);
    }
}


