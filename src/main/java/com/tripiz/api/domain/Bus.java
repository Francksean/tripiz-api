package com.tripiz.api.domain;

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
@Table(name = "bus")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bus_id")
    private UUID busId;

    @Column(name = "bus_number")
    private Integer busNumber;

    @Column(name = "matriculation")
    private String matriculation;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "status")
    private String status;
}



