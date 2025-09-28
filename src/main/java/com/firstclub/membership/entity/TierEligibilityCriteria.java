package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents the eligibility criteria for a membership tier
 */
@Entity
@Table(name = "tier_eligibility_criteria", indexes = {
    @Index(name = "idx_criteria_tier", columnList = "tier_id"),
    @Index(name = "idx_criteria_type", columnList = "criteria_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tier"})
@EqualsAndHashCode(callSuper = true, exclude = {"tier"})
public class TierEligibilityCriteria extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType; // ORDER_COUNT, ORDER_VALUE, COHORT

    @Column(name = "criteria_value", nullable = false)
    private String criteriaValue;

    @Column(name = "comparison_operator")
    private String comparisonOperator; // GT, GTE, LT, LTE, EQ, IN

    @Column(length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "evaluation_period_days")
    private Integer evaluationPeriodDays; // For time-based criteria

    /**
     * Parse criteria value as numeric for comparison
     */
    @Transient
    public Double getNumericValue() {
        try {
            return Double.parseDouble(criteriaValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Check if a value meets this criteria
     */
    public boolean isMet(Object value) {
        if (value == null || comparisonOperator == null) {
            return false;
        }

        switch (criteriaType) {
            case "ORDER_COUNT":
            case "ORDER_VALUE":
                return evaluateNumericCriteria(value);
            case "COHORT":
                return evaluateStringCriteria(value);
            default:
                return false;
        }
    }

    private boolean evaluateNumericCriteria(Object value) {
        Double criteriaNumeric = getNumericValue();
        if (criteriaNumeric == null) return false;

        Double valueNumeric;
        if (value instanceof Number) {
            valueNumeric = ((Number) value).doubleValue();
        } else {
            try {
                valueNumeric = Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return false;
            }
        }

        switch (comparisonOperator) {
            case "GT": return valueNumeric > criteriaNumeric;
            case "GTE": return valueNumeric >= criteriaNumeric;
            case "LT": return valueNumeric < criteriaNumeric;
            case "LTE": return valueNumeric <= criteriaNumeric;
            case "EQ": return valueNumeric.equals(criteriaNumeric);
            default: return false;
        }
    }

    private boolean evaluateStringCriteria(Object value) {
        String valueStr = value.toString();
        switch (comparisonOperator) {
            case "EQ": return valueStr.equals(criteriaValue);
            case "IN": return criteriaValue.contains(valueStr);
            default: return false;
        }
    }
} 