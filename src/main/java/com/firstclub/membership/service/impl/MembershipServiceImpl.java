package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreateMemberRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.*;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.service.MembershipService;
import com.firstclub.membership.service.TierEligibilityEngine;
import com.firstclub.membership.util.MembershipMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Implementation of membership service with thread-safe operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final OrderStatisticsRepository orderStatisticsRepository;
    private final TierEligibilityEngine tierEligibilityEngine;
    private final MembershipMapper mapper;
    
    // Thread-safe locks for critical sections
    private final ReentrantReadWriteLock memberLock = new ReentrantReadWriteLock();

    @Override
    @Transactional
    public MembershipStatusDto createMember(CreateMemberRequest request) {
        log.info("Creating new member with userId: {}", request.getUserId());
        
        // Check if member already exists
        if (memberRepository.existsByUserId(request.getUserId())) {
            throw new MembershipException("Member already exists with userId: " + request.getUserId());
        }
        
        // Create member
        Member member = Member.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .cohort(request.getCohort())
                .build();
        
        // Create order statistics
        OrderStatistics stats = OrderStatistics.builder()
                .member(member)
                .lastUpdated(LocalDateTime.now())
                .monthlyStatsResetDate(LocalDateTime.now())
                .build();
        
        member.setOrderStatistics(stats);
        member = memberRepository.save(member);
        
        return mapper.toMembershipStatusDto(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanDto> getAllPlans() {
        return planRepository.findAllAvailable().stream()
                .map(mapper::toPlanDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipTierDto> getAllTiers() {
        return tierRepository.findAllWithBenefits().stream()
                .map(mapper::toTierDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubscriptionDto subscribe(SubscribeRequest request) {
        log.info("Processing subscription for userId: {}", request.getUserId());
        
        memberLock.writeLock().lock();
        try {
            // Load required entities sequentially within the same transactional thread to keep them managed
            Member member = memberRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new MembershipException("Member not found"));
            
            MembershipPlan plan = planRepository.findByPlanType(request.getPlanType())
                    .orElseThrow(() -> new MembershipException("Plan not found"));
            
            MembershipTier tier = tierRepository.findByTierType(request.getTierType())
                    .orElseThrow(() -> new MembershipException("Tier not found"));
            
            // Check for existing active subscription
            if (member.hasActiveSubscription()) {
                throw new MembershipException("Member already has an active subscription");
            }
            
            // Check tier eligibility (depends on both member and tier)
            if (!tierEligibilityEngine.isEligibleForTier(member, tier)) {
                throw new MembershipException("Member is not eligible for the requested tier");
            }
            
            // Create subscription
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(plan.getDurationInMonths());
            
            Subscription subscription = Subscription.builder()
                    .member(member)
                    .plan(plan)
                    .tier(tier)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(SubscriptionStatus.ACTIVE)
                    .price(plan.getBasePrice())
                    .autoRenew(request.getAutoRenew())
                    .nextBillingDate(endDate)
                    .build();
            
            subscription = subscriptionRepository.save(subscription);
            
            log.info("Subscription created successfully for userId: {}", request.getUserId());
            return mapper.toSubscriptionDto(subscription);
            
        } finally {
            memberLock.writeLock().unlock();
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubscriptionDto upgradeTier(TierChangeRequest request) {
        log.info("Processing tier upgrade for userId: {}", request.getUserId());
        
        return changeTier(request, true);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubscriptionDto downgradeTier(TierChangeRequest request) {
        log.info("Processing tier downgrade for userId: {}", request.getUserId());
        
        return changeTier(request, false);
    }

    private SubscriptionDto changeTier(TierChangeRequest request, boolean isUpgrade) {
        memberLock.writeLock().lock();
        try {
            // Get member with active subscription
            Member member = memberRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new MembershipException("Member not found"));
            
            // Get active subscription with lock
            Subscription subscription = subscriptionRepository
                    .findActiveByMemberIdWithLock(member.getId())
                    .orElseThrow(() -> new MembershipException("No active subscription found"));
            
            // Get target tier
            MembershipTier targetTier = tierRepository.findByTierType(request.getTargetTierType())
                    .orElseThrow(() -> new MembershipException("Target tier not found"));
            
            // Validate tier change
            if (isUpgrade && !targetTier.getTierType().isHigherThan(subscription.getTier().getTierType())) {
                throw new MembershipException("Target tier must be higher than current tier for upgrade");
            }
            
            if (!isUpgrade && !targetTier.getTierType().isLowerThan(subscription.getTier().getTierType())) {
                throw new MembershipException("Target tier must be lower than current tier for downgrade");
            }
            
            // Check eligibility for upgrade
            if (isUpgrade && !tierEligibilityEngine.isEligibleForTier(member, targetTier)) {
                throw new MembershipException("Member is not eligible for the target tier");
            }
            
            // Update subscription
            subscription.setTier(targetTier);
            subscription = subscriptionRepository.save(subscription);
            
            log.info("Tier {} successful for userId: {}", isUpgrade ? "upgrade" : "downgrade", request.getUserId());
            return mapper.toSubscriptionDto(subscription);
            
        } finally {
            memberLock.writeLock().unlock();
        }
    }

    @Override
    @Transactional
    public void cancelSubscription(String userId, String reason) {
        log.info("Cancelling subscription for userId: {}", userId);
        
        memberLock.writeLock().lock();
        try {
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new MembershipException("Member not found"));
            
            Subscription subscription = subscriptionRepository
                    .findActiveByMemberIdWithLock(member.getId())
                    .orElseThrow(() -> new MembershipException("No active subscription found"));
            
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setCancelledAt(LocalDateTime.now());
            subscription.setCancellationReason(reason);
            subscription.setAutoRenew(false);
            
            subscriptionRepository.save(subscription);
            
            log.info("Subscription cancelled for userId: {}", userId);
            
        } finally {
            memberLock.writeLock().unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipStatusDto getMemberStatus(String userId) {
        memberLock.readLock().lock();
        try {
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new MembershipException("Member not found"));
            
            return mapper.toMembershipStatusDto(member);
            
        } finally {
            memberLock.readLock().unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipTierDto> checkTierEligibility(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MembershipException("Member not found"));
        
        return tierEligibilityEngine.findEligibleTiers(member).stream()
                .map(mapper::toTierDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitDto> getMemberBenefits(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MembershipException("Member not found"));
        
        Subscription activeSubscription = member.getActiveSubscription();
        if (activeSubscription == null) {
            return List.of();
        }
        
        return activeSubscription.getTier().getBenefits().stream()
                .filter(Benefit::getIsActive)
                .map(mapper::toBenefitDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void recordOrder(String userId, BigDecimal orderValue) {
        log.debug("Recording order for userId: {} with value: {}", userId, orderValue);
        
        try {
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new MembershipException("Member not found"));
            
            OrderStatistics stats = orderStatisticsRepository
                    .findByMemberIdWithLock(member.getId())
                    .orElse(OrderStatistics.builder()
                            .member(member)
                            .lastUpdated(LocalDateTime.now())
                            .monthlyStatsResetDate(LocalDateTime.now())
                            .build());

            // Check if monthly reset is needed
            if (stats.needsMonthlyReset()) {
                stats.resetMonthlyStats();
            }
            
            // Record the order
            stats.recordOrder(orderValue);
            
            // Update member lifetime value
            member.setTotalLifetimeValue(member.getTotalLifetimeValue().add(orderValue));
            
            orderStatisticsRepository.save(stats);
            memberRepository.save(member);
            
            log.debug("Order recorded successfully for userId: {}", userId);
            
        } catch (Exception e) {
            log.error("Error recording order for userId: {}", userId, e);
            throw e; // Re-throw to ensure transaction rollback
        }
    }
} 