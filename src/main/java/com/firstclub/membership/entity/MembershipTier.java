package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipTierType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a membership tier with its associated benefits
 */
@Entity
@Table(name = "membership_tiers",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tierType"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"benefits", "eligibilityCriteria"})
@EqualsAndHashCode(callSuper = true, exclude = {"benefits", "eligibilityCriteria"})
public class MembershipTier extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private MembershipTierType tierType;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer level;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Benefit> benefits = new HashSet<>();

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<TierEligibilityCriteria> eligibilityCriteria = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Add a benefit to this tier
     */
    public void addBenefit(Benefit benefit) {
        benefits.add(benefit);
        benefit.setTier(this);
    }

    /**
     * Add an eligibility criteria to this tier
     */
    public void addEligibilityCriteria(TierEligibilityCriteria criteria) {
        eligibilityCriteria.add(criteria);
        criteria.setTier(this);
    }
} 