package com.tripiz.api.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private long activeUsers;
    private long activeBuses;
    private long tripsToday;
    private double revenueToday;
    private List<RevenueEvolutionDTO> revenueLast7Days;
    private List<PaymentMethodUsageDTO> paymentMethodUsage;
    private long ticketsSoldToday;
    private long ticketsUsedToday;
    private long ticketsExpiredToday;
    private double ticketUsageRate; // pourcentage
    private List<TripPassengerDTO> salesByLine;
    private double totalWalletBalance;
    private long walletRechargesToday;
    private double rechargeAmountToday;
    private long activeWallets;
    private List<RechargeEvolutionDTO> rechargeEvolutionLastWeek;
}

