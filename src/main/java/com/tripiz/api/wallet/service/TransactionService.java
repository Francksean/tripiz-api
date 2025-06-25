package com.tripiz.api.wallet.service;

import com.google.gson.Gson;
import com.tripiz.api.model.PaymentRequestDTO;
import com.tripiz.api.model.RechargeRequestDTO;
import com.tripiz.api.wallet.domain.*;
import com.tripiz.api.wallet.enums.TransactionStatus;
import com.tripiz.api.wallet.exceptions.InsufficientFundsException;
import com.tripiz.api.wallet.exceptions.PaymentProcessingException;
import com.tripiz.api.wallet.repositories.BalanceHistoryRepository;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import com.tripiz.api.wallet.types.NotchPayTransaction;
import com.tripiz.api.wallet.types.NotchPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BalanceHistoryRepository balanceHistoryRepository;
    @Autowired
    Gson gson;

    @Value("${notchpay.key.public}")
    private String notchpayPublicKey;

    @Transactional
    public Recharge initiateRecharge(RechargeRequestDTO request) {
        Wallet wallet = walletRepository.findById(request.getWalletId()).orElseThrow();

        Recharge recharge = new Recharge();
        recharge.setAmount(request.getAmount());
        recharge.setWallet(wallet);
        recharge.setTimestamp(LocalDateTime.now());
        recharge.setReference(UUID.randomUUID().toString());
        recharge.setStatus(TransactionStatus.PENDING);
        recharge.setRechargerNumber(request.getPhone());
        recharge.setChannel(request.getChannel());

        // Sauvegarder d'abord la transaction
        recharge = transactionRepository.save(recharge);

        try {
            processNotchPayRecharge(recharge);
            return recharge;
        } catch (Exception e) {
            recharge.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(recharge);
            throw new PaymentProcessingException("Échec du traitement de la recharge", e);
        }
    }

    private void processNotchPayRecharge(Recharge recharge) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reference", recharge.getReference());
        payload.put("amount", recharge.getAmount());
        payload.put("phone", recharge.getRechargerNumber());
        payload.put("currency", "XAF");
        payload.put("description", "Recharge de portefeuille");

        HttpResponse<String> createResponse = createNotchPayment(payload);

        if (createResponse.statusCode() != 201) {
            throw new PaymentProcessingException("Échec de la création du paiement: " + createResponse.body());
        }

        NotchPaymentResponse responseData = gson.fromJson(createResponse.body(), NotchPaymentResponse.class);
        NotchPayTransaction transaction = responseData.getTransaction();
        String paymentReference = transaction.getReference();

        // Préparer les données de finalisation
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("channel", recharge.getChannel());

        Map<String, String> data = new HashMap<>();
        data.put("phone", recharge.getRechargerNumber());
        paymentDetails.put("data", data);

        HttpResponse<String> finalizeResponse = finalizePayment(paymentReference, paymentDetails);

        if (finalizeResponse.statusCode() != 200) {
            throw new PaymentProcessingException("Échec de la finalisation du paiement: " + finalizeResponse.body());
        }

        NotchPaymentResponse paymentResult = gson.fromJson(finalizeResponse.body(), NotchPaymentResponse.class);

        if (!"Accepted".equals(paymentResult.getStatus())) {
            throw new PaymentProcessingException("Paiement refusé: " + paymentResult.getMessage());
        }

        // Mettre à jour la transaction avec les infos de NotchPay
        recharge.setPaymentGatewayReference(paymentResult.getTransaction().getId());
        recharge.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(recharge);

        // Mettre à jour le solde du wallet
        Wallet wallet = recharge.getWallet();
        double oldBalance = wallet.getBalance();
        wallet.setBalance(oldBalance + recharge.getAmount());
        walletRepository.save(wallet);

        // Enregistrer l'historique du solde
        saveBalanceHistory(wallet, oldBalance, "RECHARGE");
    }

    private HttpResponse<String> createNotchPayment(Map<String, Object> data) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.notchpay.co/payments/"))
                .header("Content-Type", "application/json")
                .header("Authorization", notchpayPublicKey)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> finalizePayment(String reference, Map<String, Object> data) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://api.notchpay.co/payments/" + reference;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", notchpayPublicKey)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Transactional
    public Spending initiatePayment(PaymentRequestDTO request) {
        Wallet wallet = walletRepository.findById(request.getWalletId()).orElseThrow();

        if (wallet.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Solde insuffisant");
        }

        Spending spending = new Spending();
        spending.setAmount(request.getAmount());
        spending.setWallet(wallet);
        spending.setTimestamp(LocalDateTime.now());
        spending.setReference(UUID.randomUUID().toString());
        spending.setStatus(TransactionStatus.PENDING);

        return transactionRepository.save(spending);
    }

    @Transactional
    public void completePayment(Long paymentId) {
        Spending spending = (Spending) transactionRepository.findById(paymentId)
                .orElseThrow();

        if (spending.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction déjà traitée");
        }

        Wallet wallet = spending.getWallet();
        double oldBalance = wallet.getBalance();

        spending.setStatus(TransactionStatus.COMPLETE);
        wallet.setBalance(oldBalance - spending.getAmount());

        transactionRepository.save(spending);
        walletRepository.save(wallet);

        saveBalanceHistory(wallet, oldBalance, "PAIEMENT");
    }

    @Transactional
    public void completeTransaction(Long transactionId, boolean success) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow();

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction déjà traitée");
        }

        Wallet wallet = transaction.getWallet();
        double oldBalance = wallet.getBalance();

        if (success) {
            transaction.setStatus(TransactionStatus.COMPLETE);

            if (transaction instanceof Recharge) {
                wallet.setBalance(oldBalance + transaction.getAmount());
            } else if (transaction instanceof Spending) {
                wallet.setBalance(oldBalance - transaction.getAmount());
            }

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