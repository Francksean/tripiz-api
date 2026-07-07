package com.tripiz.api.dashboard.service;

import com.tripiz.api.dashboard.dto.*;
import com.tripiz.api.repository.BusRepository;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.ticket.repository.TicketRepository;
import com.tripiz.api.wallet.repositories.BalanceHistoryRepository;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public DashboardStatsDTO getDashboardStats(DashboardFilters filters) {
        if (filters == null) {
            filters = new DashboardFilters();
            filters.setStartDate(LocalDateTime.now());
            filters.setEndDate(LocalDateTime.now());
        }

        LocalDateTime start = filters.getStartDate();
        LocalDateTime end = filters.getEndDate();

        long activeUsers = userRepository.countByStatusIgnoreCase("ONLINE");
        long activeBuses = busRepository.countByStatusIgnoreCase("En service");

        LocalDateTime todayStart = LocalDateTime.now();
        LocalDateTime todayEnd = LocalDateTime.now();
        long tripsToday = tripRepository.countByTripDateBetween(todayStart, todayEnd);

        Double revenueToday = ticketRepository.sumPriceByPurchaseDateBetween(todayStart, todayEnd);
        if (revenueToday == null) revenueToday = 0.0;

        List<RevenueEvolutionDTO> revenueLast7Days = getRevenueEvolution(7);

        List<PaymentMethodUsageDTO> paymentUsage = getPaymentMethodUsage(start, end);

        long ticketsSoldToday = ticketRepository.countByPurchaseDateBetween(todayStart, todayEnd);
        long ticketsUsedToday = ticketRepository.countByStatusAndUseDateBetween("USED", todayStart, todayEnd);
        long ticketsExpiredToday = ticketRepository.countByStatusAndUseDateBetween("EXPIRED", todayStart, todayEnd);

        long totalTickets = ticketRepository.countByPurchaseDateBetween(start, end);
        long usedTickets = ticketRepository.countByStatusAndUseDateBetween("USED", start, end);
        double usageRate = totalTickets > 0 ? (double) usedTickets / totalTickets * 100 : 0.0;

        List<TripPassengerDTO> salesByLine = getSalesByLine(start, end);

        Double totalWalletBalance = walletRepository.findAll().stream()
                .mapToDouble(w -> w.getBalance())
                .sum();

        long rechargesToday = transactionRepository.countByTransactionTypeAndTimestampBetween(
                "RECHARGE", todayStart, todayEnd
        );

        Double rechargeAmountToday = transactionRepository.sumAmountByTransactionTypeAndTimestampBetween(
                "RECHARGE", todayStart, todayEnd
        );
        if (rechargeAmountToday == null) rechargeAmountToday = 0.0;

        long activeWallets = walletRepository.findAll().stream()
                .filter(w -> w.getBalance() > 0)
                .count();

        List<RechargeEvolutionDTO> rechargeEvolution = getRechargeEvolution(7);

        return DashboardStatsDTO.builder()
                .activeUsers(activeUsers)
                .activeBuses(activeBuses)
                .tripsToday(tripsToday)
                .revenueToday(revenueToday)
                .revenueLast7Days(revenueLast7Days)
                .paymentMethodUsage(paymentUsage)
                .ticketsSoldToday(ticketsSoldToday)
                .ticketsUsedToday(ticketsUsedToday)
                .ticketsExpiredToday(ticketsExpiredToday)
                .ticketUsageRate(usageRate)
                .salesByLine(salesByLine)
                .totalWalletBalance(totalWalletBalance)
                .walletRechargesToday(rechargesToday)
                .rechargeAmountToday(rechargeAmountToday)
                .activeWallets(activeWallets)
                .rechargeEvolutionLastWeek(rechargeEvolution)
                .build();
    }

    private List<RevenueEvolutionDTO> getRevenueEvolution(int days) {
        List<RevenueEvolutionDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime dayEnd = now.minusDays(i).with(LocalTime.MAX);
            Double revenue = ticketRepository.sumPriceByPurchaseDateBetween(dayStart, dayEnd);
            if (revenue == null) revenue = 0.0;
            result.add(RevenueEvolutionDTO.builder()
                    .day(dayStart.format(formatter))
                    .revenue(revenue)
                    .build());
        }
        return result;
    }

    private List<PaymentMethodUsageDTO> getPaymentMethodUsage(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = ticketRepository.countByPaymentMethodBetween(start, end);
        long total = results.stream().mapToLong(r -> (Long) r[1]).sum();
        return results.stream().map(r -> {
            String method = (String) r[0];
            long count = (Long) r[1];
            double percentage = total > 0 ? (double) count / total * 100 : 0.0;
            return PaymentMethodUsageDTO.builder()
                    .paymentMethod(method)
                    .count(count)
                    .percentage(percentage)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<TripPassengerDTO> getSalesByLine(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = ticketRepository.countByTripIdBetween(start, end);
        return results.stream().map(r -> {
            UUID tripId = (UUID) r[0];
            long count = (Long) r[1];
            return TripPassengerDTO.builder()
                    .tripId(tripId)
                    .tripName("Trip " + tripId.toString().substring(0, 8))
                    .passengerCount(count)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<RechargeEvolutionDTO> getRechargeEvolution(int days) {
        List<RechargeEvolutionDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).with(LocalTime.MIN);
            LocalDateTime dayEnd = now.minusDays(i).with(LocalTime.MAX);
            long count = transactionRepository.countByTransactionTypeAndTimestampBetween("RECHARGE", dayStart, dayEnd);
            result.add(RechargeEvolutionDTO.builder()
                    .day(dayStart.format(formatter))
                    .rechargeCount(count)
                    .build());
        }
        return result;
    }
}