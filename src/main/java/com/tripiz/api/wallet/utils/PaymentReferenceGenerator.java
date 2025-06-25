package com.tripiz.api.wallet.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PaymentReferenceGenerator {
    public static String generatePaymentReference() {
        // Génère un UUID standard
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // Convertit en alphanumérique étendu
        StringBuilder sb = new StringBuilder();
        for (char c : uuid.toCharArray()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }

        // Réinsère les tirets dans les positions spécifiques
        return formatReference(sb.toString());
    }

    private static String formatReference(String input) {
        return input.substring(0, 8) + '-' +
                input.substring(8, 12) + '-' +
                input.substring(12, 16) + '-' +
                input.substring(16, 20) + '-' +
                input.substring(20);
    }
}