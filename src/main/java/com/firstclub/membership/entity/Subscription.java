package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipTierType;
import com.firstclub.membership.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a member's subscription to a membership plan and tier
 */
@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_subscription_member", columnList = "member_id"),
    @Index(name = "idx_subscription_status", columnList = "status"),
    @Index(name = "idx_subscription_dates", columnList = "startDate,endDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"member"})
@EqualsAndHashCode(callSuper = true, exclude = {"member"})
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoRenew = true;

    @Column
    private LocalDateTime nextBillingDate;

    @Column
    private String cancellationReason;

    /**
     * Check if the subscription is currently active
     */
    @Transient
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == SubscriptionStatus.ACTIVE &&
               now.isAfter(startDate) &&
               now.isBefore(endDate);
    }

    /**
     * Check if the subscription can be upgraded
     */
    @Transient
    public boolean canUpgrade() {
        return isCurrentlyActive() && tier.getTierType() != MembershipTierType.PLATINUM;
    }

    /**
     * Check if the subscription can be downgraded
     */
    @Transient
    public boolean canDowngrade() {
        return isCurrentlyActive() && tier.getTierType() != MembershipTierType.SILVER;
    }

    /**
     * Calculate remaining days in subscription
     */
    @Transient
    public long getRemainingDays() {
        if (!isCurrentlyActive()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), endDate).toDays();
    }
} 