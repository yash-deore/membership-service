package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.enums.MembershipTierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MembershipTier entity
 */
@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    /**
     * Find a tier by tier type
     */
    Optional<MembershipTier> findByTierType(MembershipTierType tierType);

    /**
     * Find tiers with their benefits eagerly loaded
     */
    @Query("SELECT DISTINCT t FROM MembershipTier t LEFT JOIN FETCH t.benefits WHERE t.isAvailable = true AND t.active = true ORDER BY t.level")
    List<MembershipTier> findAllWithBenefits();

    /**
     * Find tiers with their eligibility criteria eagerly loaded
     */
    @Query("SELECT DISTINCT t FROM MembershipTier t LEFT JOIN FETCH t.eligibilityCriteria WHERE t.isAvailable = true AND t.active = true ORDER BY t.level")
    List<MembershipTier> findAllWithEligibilityCriteria();
} 