package com.firstclub.membership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for member's current membership status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipStatusDto {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean hasActiveSubscription;
    private SubscriptionDto activeSubscription;
    private List<BenefitDto> activeBenefits;
    private BigDecimal totalLifetimeValue;
    private OrderStatisticsDto orderStatistics;
} 