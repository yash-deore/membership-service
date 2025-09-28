package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipPlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a membership plan configuration
 */
@Entity
@Table(name = "membership_plans", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"planType"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class MembershipPlan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private MembershipPlanType planType;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer durationInMonths;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Calculate the monthly price for this plan
     */
    @Transient
    public BigDecimal getMonthlyPrice() {
        return basePrice.divide(BigDecimal.valueOf(durationInMonths), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate discount percentage compared to monthly plan
     */
    public BigDecimal getDiscountPercentage(BigDecimal monthlyPlanPrice) {
        BigDecimal totalMonthlyPrice = monthlyPlanPrice.multiply(BigDecimal.valueOf(durationInMonths));
        BigDecimal discount = totalMonthlyPrice.subtract(basePrice);
        return discount.divide(totalMonthlyPrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}