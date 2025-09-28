package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for subscription information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {

    private Long id;
    private String userId;
    private MembershipPlanDto plan;
    private MembershipTierDto tier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private BigDecimal price;
    private Boolean autoRenew;
    private Long remainingDays;
    private Boolean canUpgrade;
    private Boolean canDowngrade;
} 