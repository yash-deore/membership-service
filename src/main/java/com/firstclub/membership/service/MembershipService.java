package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateMemberRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.*;

import java.util.List;

/**
 * Main service interface for membership operations
 */
public interface MembershipService {

    /**
     * Create a new member
     */
    MembershipStatusDto createMember(CreateMemberRequest request);

    /**
     * Get all available membership plans
     */
    List<MembershipPlanDto> getAllPlans();

    /**
     * Get all available membership tiers
     */
    List<MembershipTierDto> getAllTiers();

    /**
     * Subscribe a member to a plan and tier
     */
    SubscriptionDto subscribe(SubscribeRequest request);

    /**
     * Upgrade a member's tier
     */
    SubscriptionDto upgradeTier(TierChangeRequest request);

    /**
     * Downgrade a member's tier
     */
    SubscriptionDto downgradeTier(TierChangeRequest request);

    /**
     * Cancel a member's subscription
     */
    void cancelSubscription(String userId, String reason);

    /**
     * Get member's current status
     */
    MembershipStatusDto getMemberStatus(String userId);

    /**
     * Check member's tier eligibility
     */
    List<MembershipTierDto> checkTierEligibility(String userId);

    /**
     * Get member's active benefits
     */
    List<BenefitDto> getMemberBenefits(String userId);

    /**
     * Record an order for a member (updates statistics)
     */
    void recordOrder(String userId, java.math.BigDecimal orderValue);
} 