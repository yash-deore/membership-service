package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different states of a membership subscription
 */
@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    ACTIVE("Active"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    SUSPENDED("Suspended");

    private final String displayName;
} 