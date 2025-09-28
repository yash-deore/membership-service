package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tracks order statistics for a member used in tier eligibility calculations
 */
@Entity
@Table(name = "order_statistics", indexes = {
    @Index(name = "idx_order_stats_member", columnList = "member_id", unique = true),
    @Index(name = "idx_order_stats_updated", columnList = "lastUpdated")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member"})
@EqualsAndHashCode(callSuper = true, exclude = {"member"})
public class OrderStatistics extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrderCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer monthlyOrderCount = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalOrderValue = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal monthlyOrderValue = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal averageOrderValue = BigDecimal.ZERO;

    @Column
    private LocalDateTime firstOrderDate;

    @Column
    private LocalDateTime lastOrderDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private LocalDateTime monthlyStatsResetDate;

    /**
     * Update statistics with a new order
     */
    public void recordOrder(BigDecimal orderValue) {
        LocalDateTime now = LocalDateTime.now();
        
        // Update counts
        totalOrderCount++;
        monthlyOrderCount++;
        
        // Update values
        totalOrderValue = totalOrderValue.add(orderValue);
        monthlyOrderValue = monthlyOrderValue.add(orderValue);
        
        // Update average
        averageOrderValue = totalOrderValue.divide(
            BigDecimal.valueOf(totalOrderCount), 2, BigDecimal.ROUND_HALF_UP
        );
        
        // Update dates
        if (firstOrderDate == null) {
            firstOrderDate = now;
        }
        lastOrderDate = now;
        lastUpdated = now;
    }

    /**
     * Reset monthly statistics
     */
    public void resetMonthlyStats() {
        monthlyOrderCount = 0;
        monthlyOrderValue = BigDecimal.ZERO;
        monthlyStatsResetDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    /**
     * Check if monthly stats need reset (called at the start of each month)
     */
    public boolean needsMonthlyReset() {
        LocalDateTime now = LocalDateTime.now();
        return monthlyStatsResetDate == null || 
               now.getMonth() != monthlyStatsResetDate.getMonth() ||
               now.getYear() != monthlyStatsResetDate.getYear();
    }
} 