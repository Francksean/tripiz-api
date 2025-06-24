package com.tripiz.api.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "route_id")
    private UUID routeId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "description")
    private String description;

    @Column(name = "ticket_price")
    private Float ticketPrice;

    @Column(name = "status")
    private String status;
}



