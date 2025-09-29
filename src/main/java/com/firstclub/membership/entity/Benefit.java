package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a benefit associated with a membership tier
 */
@Entity
@Table(name = "benefits", indexes = {
    @Index(name = "idx_benefit_tier", columnList = "tier_id"),
    @Index(name = "idx_benefit_type", columnList = "benefit_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tier"})
@EqualsAndHashCode(callSuper = true, exclude = {"tier"})
public class Benefit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "value")
    private String value;

    @Column(name = "applicable_categories")
    private String applicableCategories;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
} 