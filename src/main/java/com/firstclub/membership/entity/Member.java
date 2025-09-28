package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a member in the FirstClub system
 */
@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_member_user_id", columnList = "userId", unique = true),
    @Index(name = "idx_member_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"subscriptions", "orderStatistics"})
@EqualsAndHashCode(callSuper = true, exclude = {"subscriptions", "orderStatistics"})
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String phoneNumber;

    @Column
    private String cohort;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderStatistics orderStatistics;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalLifetimeValue = BigDecimal.ZERO;

    /**
     * Get the currently active subscription if exists
     */
    @Transient
    public Subscription getActiveSubscription() {
        return subscriptions.stream()
                .filter(s -> s.getActive() && s.isCurrentlyActive())
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if member has an active subscription
     */
    @Transient
    public boolean hasActiveSubscription() {
        return getActiveSubscription() != null;
    }
} 