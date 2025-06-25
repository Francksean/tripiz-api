package com.tripiz.api.wallet.types;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotchPaymentResponse {
    private String status;
    private String message;
    private int code;
    private NotchPayTransaction transaction;

    @SerializedName("authorization_url")
    private String authorizationUrl;
}