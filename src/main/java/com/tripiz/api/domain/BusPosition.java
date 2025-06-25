package com.tripiz.api.domain;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusPosition {

        private UUID busId;
        private double latitude;
        private double longitude;
        private PositionMessageType type; // JOIN, UPDATE, LEAVE


}
