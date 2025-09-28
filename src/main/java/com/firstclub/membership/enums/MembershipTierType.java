package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different membership tier levels
 */
@Getter
@RequiredArgsConstructor
public enum MembershipTierType {
    SILVER("Silver", 1),
    GOLD("Gold", 2),
    PLATINUM("Platinum", 3);

    private final String displayName;
    private final int level;

    /**
     * Check if this tier is higher than another tier
     */
    public boolean isHigherThan(MembershipTierType other) {
        return this.level > other.level;
    }

/**
     * Check if this tier is lower than another tier
     */
    public boolean isLowerThan(MembershipTierType other) {
        return this.level < other.level;
    }
} 