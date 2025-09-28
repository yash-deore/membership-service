package com.firstclub.membership.util;

import com.firstclub.membership.dto.response.*;
import com.firstclub.membership.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting entities to DTOs
 */
@Component
public class MembershipMapper {

    public MembershipPlanDto toPlanDto(MembershipPlan plan) {
        if (plan == null) return null;
        
        // Calculate monthly price for comparison
        MembershipPlanDto dto = MembershipPlanDto.builder()
                .id(plan.getId())
                .planType(plan.getPlanType())
                .name(plan.getName())
                .description(plan.getDescription())
                .basePrice(plan.getBasePrice())
                .durationInMonths(plan.getDurationInMonths())
                .monthlyPrice(plan.getMonthlyPrice())
                .isAvailable(plan.getIsAvailable())
                .build();
        
        // Calculate discount percentage (assuming monthly plan price is passed separately)
        // For demo, we'll set it to 0 for monthly plan
        if (plan.getDurationInMonths() > 1) {
            dto.setDiscountPercentage(plan.getDiscountPercentage(plan.getMonthlyPrice().multiply(java.math.BigDecimal.valueOf(3))));
        }
        
        return dto;
    }

    public MembershipTierDto toTierDto(MembershipTier tier) {
        if (tier == null) return null;
        
        return MembershipTierDto.builder()
                .id(tier.getId())
                .tierType(tier.getTierType())
                .name(tier.getName())
                .description(tier.getDescription())
                .level(tier.getLevel())
                .benefits(tier.getBenefits().stream()
                        .filter(Benefit::getIsActive)
                        .map(this::toBenefitDto)
                        .collect(Collectors.toList()))
                .isAvailable(tier.getIsAvailable())
                .build();
    }

    public BenefitDto toBenefitDto(Benefit benefit) {
        if (benefit == null) return null;
        
        return BenefitDto.builder()
                .id(benefit.getId())
                .benefitType(benefit.getBenefitType())
                .name(benefit.getName())
                .description(benefit.getDescription())
                .value(benefit.getValue())
                .applicableCategories(benefit.getApplicableCategories())
                .displayOrder(benefit.getDisplayOrder())
                .build();
    }

    public SubscriptionDto toSubscriptionDto(Subscription subscription) {
        if (subscription == null) return null;
        
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .userId(subscription.getMember().getUserId())
                .plan(toPlanDto(subscription.getPlan()))
                .tier(toTierDto(subscription.getTier()))
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .price(subscription.getPrice())
                .autoRenew(subscription.getAutoRenew())
                .remainingDays(subscription.getRemainingDays())
                .canUpgrade(subscription.canUpgrade())
                .canDowngrade(subscription.canDowngrade())
                .build();
    }

    public MembershipStatusDto toMembershipStatusDto(Member member) {
        if (member == null) return null;
        
        Subscription activeSubscription = member.getActiveSubscription();
        List<BenefitDto> activeBenefits = null;
        
        if (activeSubscription != null && activeSubscription.getTier() != null) {
            activeBenefits = activeSubscription.getTier().getBenefits().stream()
                    .filter(Benefit::getIsActive)
                    .map(this::toBenefitDto)
                    .collect(Collectors.toList());
        }
        
        return MembershipStatusDto.builder()
                .userId(member.getUserId())
                .email(member.getEmail())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .hasActiveSubscription(member.hasActiveSubscription())
                .activeSubscription(toSubscriptionDto(activeSubscription))
                .activeBenefits(activeBenefits)
                .totalLifetimeValue(member.getTotalLifetimeValue())
                .orderStatistics(toOrderStatisticsDto(member.getOrderStatistics()))
                .build();
    }

    public OrderStatisticsDto toOrderStatisticsDto(OrderStatistics stats) {
        if (stats == null) return null;
        
        return OrderStatisticsDto.builder()
                .totalOrderCount(stats.getTotalOrderCount())
                .monthlyOrderCount(stats.getMonthlyOrderCount())
                .totalOrderValue(stats.getTotalOrderValue())
                .monthlyOrderValue(stats.getMonthlyOrderValue())
                .averageOrderValue(stats.getAverageOrderValue())
                .firstOrderDate(stats.getFirstOrderDate())
                .lastOrderDate(stats.getLastOrderDate())
                .build();
    }
} 