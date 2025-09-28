package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.MembershipPlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MembershipPlan entity
 */
@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    /**
     * Find a plan by plan type
     */
    Optional<MembershipPlan> findByPlanType(MembershipPlanType planType);

    /**
     * Find all available plans
     */
    @Query("SELECT p FROM MembershipPlan p WHERE p.isAvailable = true AND p.active = true ORDER BY p.durationInMonths")
    List<MembershipPlan> findAllAvailable();

    /**
     * Check if a plan type exists
     */
    boolean existsByPlanType(MembershipPlanType planType);
} 