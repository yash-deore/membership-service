package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Subscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subscription entity
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Find active subscription for a member with pessimistic locking for concurrent updates
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Subscription s WHERE s.member.id = :memberId AND s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByMemberIdWithLock(@Param("memberId") Long memberId);

    /**
     * Find active subscription for a member
     */
    @Query("SELECT s FROM Subscription s WHERE s.member.id = :memberId AND s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveByMemberId(@Param("memberId") Long memberId);

    /**
     * Find all subscriptions for a member
     */
    List<Subscription> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    /**
     * Find subscriptions by status
     */
    List<Subscription> findByStatus(SubscriptionStatus status);

    /**
     * Find subscriptions expiring soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate BETWEEN :now AND :expiryDate AND s.autoRenew = false")
    List<Subscription> findExpiringSoon(@Param("now") LocalDateTime now, @Param("expiryDate") LocalDateTime expiryDate);

    /**
     * Find subscriptions that need to be renewed
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate <= CURRENT_TIMESTAMP AND s.autoRenew = true")
    List<Subscription> findSubscriptionsToRenew();

    /**
     * Count active subscriptions by tier
     */
    @Query("SELECT s.tier.tierType, COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' GROUP BY s.tier.tierType")
    List<Object[]> countActiveSubscriptionsByTier();
} 