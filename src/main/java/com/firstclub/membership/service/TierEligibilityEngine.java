package com.firstclub.membership.service;

import com.firstclub.membership.entity.Member;
import com.firstclub.membership.entity.MembershipTier;

import java.util.List;

/**
 * Engine for determining tier eligibility based on configurable criteria
 */
public interface TierEligibilityEngine {

    /**
     * Find all eligible tiers for a member based on current criteria
     */
    List<MembershipTier> findEligibleTiers(Member member);

    /**
     * Check if a member is eligible for a specific tier
     */
    boolean isEligibleForTier(Member member, MembershipTier tier);
} 