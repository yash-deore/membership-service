package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different types of benefits available in membership tiers
 */
@Getter
@RequiredArgsConstructor
public enum BenefitType {
    PERCENTAGE_DISCOUNT("Percentage Discount", "PERCENTAGE"),
    FREE_DELIVERY("Free Delivery", "BOOLEAN"),
    EXCLUSIVE_DEALS("Exclusive Deals Access", "BOOLEAN"),
    EARLY_ACCESS("Early Access to Sales", "BOOLEAN"),
    PRIORITY_SUPPORT("Priority Customer Support", "BOOLEAN"),
    CASHBACK("Cashback", "PERCENTAGE"),
    BONUS_POINTS("Bonus Loyalty Points", "MULTIPLIER");

    private final String displayName;
    private final String valueType;
} 