package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a benefit associated with a membership tier
 */
@Entity
@Table(name = "benefits", indexes = {
    @Index(name = "idx_benefit_tier", columnList = "tier_id"),
    @Index(name = "idx_benefit_type", columnList = "benefit_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tier"})
@EqualsAndHashCode(callSuper = true, exclude = {"tier"})
public class Benefit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "value")
    private String value;

    @Column(name = "applicable_categories")
    private String applicableCategories;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    /**
     * Get the benefit value as a Double for percentage/multiplier types
     */
    @Transient
    public Double getNumericValue() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get the benefit value as a Boolean for boolean types
     */
    @Transient
    public Boolean getBooleanValue() {
        return value != null && value.equalsIgnoreCase("true");
    }

    /**
     * Check if this benefit applies to a specific category
     */
    public boolean appliesToCategory(String category) {
        if (applicableCategories == null || applicableCategories.isEmpty()) {
            return true; // Applies to all categories
        }
        String[] categories = applicableCategories.split(",");
        for (String cat : categories) {
            if (cat.trim().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }
} 