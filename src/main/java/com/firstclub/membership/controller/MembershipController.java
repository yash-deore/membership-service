package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateMemberRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.*;
import com.firstclub.membership.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for membership management operations
 */
@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Membership Management", description = "APIs for managing FirstClub memberships")
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/members")
    @Operation(summary = "Create a new member", description = "Register a new member in the system")
    public ResponseEntity<MembershipStatusDto> createMember(@Valid @RequestBody CreateMemberRequest request) {
        log.info("Creating member with userId: {}", request.getUserId());
        MembershipStatusDto response = membershipService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/plans")
    @Operation(summary = "Get all membership plans", description = "Retrieve all available membership plans")
    public ResponseEntity<List<MembershipPlanDto>> getAllPlans() {
        List<MembershipPlanDto> plans = membershipService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/tiers")
    @Operation(summary = "Get all membership tiers", description = "Retrieve all available membership tiers with benefits")
    public ResponseEntity<List<MembershipTierDto>> getAllTiers() {
        List<MembershipTierDto> tiers = membershipService.getAllTiers();
        return ResponseEntity.ok(tiers);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a membership", description = "Subscribe a member to a specific plan and tier")
    public ResponseEntity<SubscriptionDto> subscribe(@Valid @RequestBody SubscribeRequest request) {
        log.info("Subscribing userId: {} to plan: {} and tier: {}", 
                request.getUserId(), request.getPlanType(), request.getTierType());
        SubscriptionDto subscription = membershipService.subscribe(request);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/upgrade")
    @Operation(summary = "Upgrade membership tier", description = "Upgrade a member's tier to a higher level")
    public ResponseEntity<SubscriptionDto> upgradeTier(@Valid @RequestBody TierChangeRequest request) {
        log.info("Upgrading tier for userId: {} to: {}", request.getUserId(), request.getTargetTierType());
        SubscriptionDto subscription = membershipService.upgradeTier(request);
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/downgrade")
    @Operation(summary = "Downgrade membership tier", description = "Downgrade a member's tier to a lower level")
    public ResponseEntity<SubscriptionDto> downgradeTier(@Valid @RequestBody TierChangeRequest request) {
        log.info("Downgrading tier for userId: {} to: {}", request.getUserId(), request.getTargetTierType());
        SubscriptionDto subscription = membershipService.downgradeTier(request);
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/cancel/{userId}")
    @Operation(summary = "Cancel membership", description = "Cancel a member's active subscription")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "User requested cancellation") String reason) {
        log.info("Cancelling subscription for userId: {}", userId);
        membershipService.cancelSubscription(userId, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{userId}")
    @Operation(summary = "Get membership status", description = "Get current membership status for a member")
    public ResponseEntity<MembershipStatusDto> getMemberStatus(@PathVariable String userId) {
        MembershipStatusDto status = membershipService.getMemberStatus(userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/eligibility/{userId}")
    @Operation(summary = "Get all eligible tiers", description = "Check which tiers a member is eligible for")
    public ResponseEntity<List<MembershipTierDto>> getAllEligibleTiers(@PathVariable String userId) {
        List<MembershipTierDto> eligibleTiers = membershipService.checkTierEligibility(userId);
        return ResponseEntity.ok(eligibleTiers);
    }

    @GetMapping("/benefits/{userId}")
    @Operation(summary = "Get member benefits", description = "Get active benefits for a member")
    public ResponseEntity<List<BenefitDto>> getMemberBenefits(@PathVariable String userId) {
        List<BenefitDto> benefits = membershipService.getMemberBenefits(userId);
        return ResponseEntity.ok(benefits);
    }

    @PostMapping("/orders/{userId}")
    @Operation(summary = "Record an order", description = "Record an order for a member to update statistics")
    public ResponseEntity<Void> recordOrder(
            @PathVariable String userId,
            @RequestParam BigDecimal orderValue) {
        log.info("Recording order for userId: {} with value: {}", userId, orderValue);
        membershipService.recordOrder(userId, orderValue);
        return ResponseEntity.accepted().build();
    }
}