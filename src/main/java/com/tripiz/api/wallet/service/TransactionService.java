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
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;

    @Autowired
    private Gson gson;

    @Value("${campay.auth-token}")
    private String campayAuthToken;

    @Value("${campay.base-url:https://demo.campay.net/api}")
    private String campayBaseUrl;

    @Transactional
    public Recharge initiateRecharge(RechargeRequestDTO request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet non trouvé"));

        Recharge recharge = new Recharge();
        recharge.setAmount(request.getAmount());
        recharge.setWallet(wallet);
        recharge.setTimestamp(LocalDateTime.now());
        recharge.setReference(UUID.randomUUID().toString());
        recharge.setStatus(TransactionStatus.PENDING);
        recharge.setRechargerNumber(request.getPhone());
        recharge.setChannel(request.getChannel());

        recharge = transactionRepository.save(recharge);

        try {
            processCampayRecharge(recharge);
            return recharge;
        } catch (Exception e) {
            recharge.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(recharge);

            throw new PaymentProcessingException(
                    "Échec du traitement de la recharge",
                    e
            );
        }
    }

    private void processCampayRecharge(Recharge recharge) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", String.valueOf((int) recharge.getAmount()));
        payload.put("currency", "XAF");
        payload.put("from", formatPhoneNumber(recharge.getRechargerNumber()));
        payload.put("description", "Recharge de portefeuille Tripiz");
        payload.put("external_reference", recharge.getReference());
        payload.put("external_user", String.valueOf(recharge.getWallet().getId()));

        HttpResponse<String> response = requestCampayPayment(payload);

        if (response.statusCode() != 200) {
            throw new PaymentProcessingException(
                    "Échec de la demande de paiement CamPay : " + response.body()
            );
        }

        Map<String, Object> responseData =
                gson.fromJson(response.body(), Map.class);

        String campayReference = (String) responseData.get("reference");

        if (campayReference == null || campayReference.isBlank()) {
            throw new PaymentProcessingException(
                    "CamPay n'a retourné aucune référence de transaction"
            );
        }

        recharge.setPaymentGatewayReference(campayReference);

        // Le paiement reste PENDING.
        // Le wallet sera crédité uniquement après le callback SUCCESSFUL.
        transactionRepository.save(recharge);
    }

    private HttpResponse<String> requestCampayPayment(
            Map<String, Object> data
    ) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(campayBaseUrl + "/collect/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Token " + campayAuthToken)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private String formatPhoneNumber(String phone) {
        String formattedPhone = phone.replaceAll("\\s+", "");

        if (formattedPhone.startsWith("+")) {
            formattedPhone = formattedPhone.substring(1);
        }

        if (!formattedPhone.startsWith("237")) {
            formattedPhone = "237" + formattedPhone;
        }

        return formattedPhone;
    }

    @Transactional
    public void handleCampayCallback(
            String status,
            String reference,
            String externalReference
    ) {
        Recharge recharge = transactionRepository
                .findByReference(externalReference)
                .filter(transaction -> transaction instanceof Recharge)
                .map(transaction -> (Recharge) transaction)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recharge non trouvée : " + externalReference
                        )
                );

        // Empêche un double crédit si CamPay renvoie plusieurs fois le callback.
        if (recharge.getStatus() != TransactionStatus.PENDING) {
            return;
        }

        if ("SUCCESSFUL".equalsIgnoreCase(status)) {
            Wallet wallet = recharge.getWallet();
            double oldBalance = wallet.getBalance();

            recharge.setStatus(TransactionStatus.COMPLETE);
            recharge.setPaymentGatewayReference(reference);

            wallet.setBalance(oldBalance + recharge.getAmount());

            transactionRepository.save(recharge);
            walletRepository.save(wallet);

            saveBalanceHistory(wallet, oldBalance, "RECHARGE");

        } else if ("FAILED".equalsIgnoreCase(status)) {
            recharge.setStatus(TransactionStatus.FAILED);
            recharge.setPaymentGatewayReference(reference);

            transactionRepository.save(recharge);
        }
    }

    @Transactional
    public Spending initiatePayment(PaymentRequestDTO request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow();

        if (wallet.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Solde insuffisant");
        }

        Spending spending = new Spending();
        spending.setAmount(request.getAmount());
        spending.setWallet(wallet);
        spending.setTimestamp(LocalDateTime.now());
        spending.setReference(UUID.randomUUID().toString());
        spending.setStatus(TransactionStatus.COMPLETE);

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

            saveBalanceHistory(
                    wallet,
                    oldBalance,
                    transaction.getClass().getSimpleName()
            );
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }

    private void saveBalanceHistory(
            Wallet wallet,
            double oldBalance,
            String reason
    ) {
        BalanceHistory history = new BalanceHistory();
        history.setWallet(wallet);
        history.setOldBalance(oldBalance);
        history.setNewBalance(wallet.getBalance());
        history.setChangeDate(LocalDateTime.now());
        history.setChangeReason(reason);

        balanceHistoryRepository.save(history);
    }
}