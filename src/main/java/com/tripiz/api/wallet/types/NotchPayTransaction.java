package com.tripiz.api.wallet.types;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotchPayTransaction {
    private String id;
    private String reference;
    private int amount;
    private String currency;
    private String status;
    private String customer;

    @SerializedName("created_at")
    private Date createdAt;
}
