package com.tripiz.api.payment.controller;

import com.tripiz.api.payment.dto.InvoiceDTO;
import com.tripiz.api.payment.dto.PaymentDataDTO;
import com.tripiz.api.payment.dto.QRPaymentRequestDTO;
import com.tripiz.api.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/generate-qr-data")
    public ResponseEntity<PaymentDataDTO> generateQRData(@RequestParam UUID ticketId) {
        PaymentDataDTO data = paymentService.generateQRData(ticketId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/process-qr-payment")
    public ResponseEntity<Void> processQRPayment(@RequestBody QRPaymentRequestDTO request) {
        paymentService.processQRPayment(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tickets/{ticketId}/invoice")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable UUID ticketId) {
        InvoiceDTO invoice = paymentService.generateInvoice(ticketId);
        return ResponseEntity.ok(invoice);
    }
}