package com.tripiz.api.domain;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPosition {
    private UUID userId;
    private double latitude;
    private double longitude;
}