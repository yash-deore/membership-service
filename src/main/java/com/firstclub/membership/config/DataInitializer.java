package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Data initializer to set up demo data on application startup
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    @Transactional
    CommandLineRunner init(MembershipPlanRepository planRepo,
                          MembershipTierRepository tierRepo) {
        return args -> {
            log.info("Initializing demo data...");

            // Create Membership Plans
            MembershipPlan monthlyPlan = MembershipPlan.builder()
                    .planType(MembershipPlanType.MONTHLY)
                    .name("Monthly Plan")
                    .description("Pay month-by-month with flexibility")
                    .basePrice(new BigDecimal("9.99"))
                    .durationInMonths(1)
                    .isAvailable(true)
                    .build();
            planRepo.save(monthlyPlan);

            MembershipPlan quarterlyPlan = MembershipPlan.builder()
                    .planType(MembershipPlanType.QUARTERLY)
                    .name("Quarterly Plan")
                    .description("Save 10% with 3-month commitment")
                    .basePrice(new BigDecimal("26.99"))
                    .durationInMonths(3)
                    .isAvailable(true)
                    .build();
            planRepo.save(quarterlyPlan);

            MembershipPlan yearlyPlan = MembershipPlan.builder()
                    .planType(MembershipPlanType.YEARLY)
                    .name("Yearly Plan")
                    .description("Best value - Save 20% with annual commitment")
                    .basePrice(new BigDecimal("95.99"))
                    .durationInMonths(12)
                    .isAvailable(true)
                    .build();
            planRepo.save(yearlyPlan);

            // Create Membership Tiers with Benefits and Eligibility Criteria
            MembershipTier silverTier = createSilverTier();
            tierRepo.save(silverTier);

            MembershipTier goldTier = createGoldTier();
            tierRepo.save(goldTier);

            MembershipTier platinumTier = createPlatinumTier();
            tierRepo.save(platinumTier);

            log.info("Demo data initialized successfully!");
            log.info("Plans created: {}", planRepo.count());
            log.info("Tiers created: {}", tierRepo.count());
        };
    }

    private MembershipTier createSilverTier() {
        MembershipTier tier = MembershipTier.builder()
                .tierType(MembershipTierType.SILVER)
                .name("Silver Tier")
                .description("Entry level membership with basic benefits")
                .level(1)
                .isAvailable(true)
                .build();

        // Add benefits
        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.FREE_DELIVERY)
                .name("Free Delivery")
                .description("Free delivery on orders above $50")
                .value("true")
                .displayOrder(1)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.PERCENTAGE_DISCOUNT)
                .name("5% Discount")
                .description("5% discount on all products")
                .value("5")
                .displayOrder(2)
                .isActive(true)
                .build());

        // No eligibility criteria - everyone can get Silver
        return tier;
    }

    private MembershipTier createGoldTier() {
        MembershipTier tier = MembershipTier.builder()
                .tierType(MembershipTierType.GOLD)
                .name("Gold Tier")
                .description("Premium membership with enhanced benefits")
                .level(2)
                .isAvailable(true)
                .build();

        // Add benefits
        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.FREE_DELIVERY)
                .name("Free Delivery")
                .description("Free delivery on all orders")
                .value("true")
                .displayOrder(1)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.PERCENTAGE_DISCOUNT)
                .name("10% Discount")
                .description("10% discount on all products")
                .value("10")
                .displayOrder(2)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.EARLY_ACCESS)
                .name("Early Access")
                .description("24-hour early access to sales")
                .value("true")
                .displayOrder(3)
                .isActive(true)
                .build());

        // Add eligibility criteria
        tier.addEligibilityCriteria(TierEligibilityCriteria.builder()
                .criteriaType("ORDER_COUNT")
                .criteriaValue("5")
                .comparisonOperator("GTE")
                .description("Minimum 5 orders required")
                .isActive(true)
                .build());

        tier.addEligibilityCriteria(TierEligibilityCriteria.builder()
                .criteriaType("ORDER_VALUE")
                .criteriaValue("500")
                .comparisonOperator("GTE")
                .description("Minimum $500 total order value")
                .isActive(true)
                .build());

        return tier;
    }

    private MembershipTier createPlatinumTier() {
        MembershipTier tier = MembershipTier.builder()
                .tierType(MembershipTierType.PLATINUM)
                .name("Platinum Tier")
                .description("Elite membership with exclusive benefits")
                .level(3)
                .isAvailable(true)
                .build();

        // Add benefits
        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.FREE_DELIVERY)
                .name("Priority Free Delivery")
                .description("Priority free delivery on all orders")
                .value("true")
                .displayOrder(1)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.PERCENTAGE_DISCOUNT)
                .name("15% Discount")
                .description("15% discount on all products")
                .value("15")
                .displayOrder(2)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.EARLY_ACCESS)
                .name("Early Access")
                .description("48-hour early access to sales")
                .value("true")
                .displayOrder(3)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .name("Exclusive Deals")
                .description("Access to Platinum-only deals")
                .value("true")
                .displayOrder(4)
                .isActive(true)
                .build());

        tier.addBenefit(Benefit.builder()
                .benefitType(BenefitType.PRIORITY_SUPPORT)
                .name("Priority Support")
                .description("24/7 priority customer support")
                .value("true")
                .displayOrder(5)
                .isActive(true)
                .build());

        // Add eligibility criteria
        tier.addEligibilityCriteria(TierEligibilityCriteria.builder()
                .criteriaType("ORDER_COUNT")
                .criteriaValue("10")
                .comparisonOperator("GTE")
                .description("Minimum 10 orders required")
                .evaluationPeriodDays(30)
                .isActive(true)
                .build());

        tier.addEligibilityCriteria(TierEligibilityCriteria.builder()
                .criteriaType("ORDER_VALUE")
                .criteriaValue("1000")
                .comparisonOperator("GTE")
                .description("Minimum $1000 monthly order value")
                .evaluationPeriodDays(30)
                .isActive(true)
                .build());

        return tier;
    }
} 