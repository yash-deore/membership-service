package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different types of membership plans available
 */
@Getter
@RequiredArgsConstructor
public enum MembershipPlanType {
    MONTHLY("Monthly", 1),
    QUARTERLY("Quarterly", 3),
    YEARLY("Yearly", 12);
    // plus the regular one that already exists in the first club system

    private final String displayName;
    private final int durationInMonths;
} 