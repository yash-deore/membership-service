package com.firstclub.membership.service.impl;

import com.firstclub.membership.entity.Member;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.OrderStatistics;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.service.TierEligibilityEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implementation of tier eligibility engine with concurrent criteria evaluation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TierEligibilityEngineImpl implements TierEligibilityEngine {

    private final MembershipTierRepository tierRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    @Transactional(readOnly = true)
    public List<MembershipTier> findEligibleTiers(Member member) {
        log.debug("Finding eligible tiers for member: {}", member.getUserId());
        
        List<MembershipTier> allTiers = tierRepository.findAllWithEligibilityCriteria();
        
        // Use concurrent processing to evaluate eligibility for each tier
        List<CompletableFuture<Optional<MembershipTier>>> futures = allTiers.stream()
                .map(tier -> CompletableFuture.supplyAsync(
                        () -> isEligibleForTier(member, tier) ? Optional.of(tier) : Optional.<MembershipTier>empty(),
                        executorService
                ))
                .collect(Collectors.toList());
        
        // Collect results
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(MembershipTier::getLevel))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEligibleForTier(Member member, MembershipTier tier) {
        if (!tier.getIsAvailable() || !tier.getActive()) {
            return false;
        }
        
        Set<TierEligibilityCriteria> criteria = tier.getEligibilityCriteria();
        if (criteria.isEmpty()) {
            return true; // No criteria means everyone is eligible
        }
        
        // Check all criteria - member must meet ALL active criteria
        for (TierEligibilityCriteria criterion : criteria) {
            if (criterion.getIsActive() && !evaluateCriterion(member, criterion)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean evaluateCriterion(Member member, TierEligibilityCriteria criterion) {
        OrderStatistics stats = member.getOrderStatistics();
        
        switch (criterion.getCriteriaType()) {
            case "ORDER_COUNT":
                if (stats == null) return false;
                Integer count = criterion.getEvaluationPeriodDays() == null ? 
                        stats.getTotalOrderCount() : stats.getMonthlyOrderCount();
                return criterion.isMet(count);
                
            case "ORDER_VALUE":
                if (stats == null) return false;
                Object value = criterion.getEvaluationPeriodDays() == null ? 
                        stats.getTotalOrderValue() : stats.getMonthlyOrderValue();
                return criterion.isMet(value);
                
            case "COHORT":
                return criterion.isMet(member.getCohort());
                
            default:
                log.warn("Unknown criteria type: {}", criterion.getCriteriaType());
                return false;
        }
    }

    /**
     * Shutdown the executor service
     */
    @jakarta.annotation.PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 