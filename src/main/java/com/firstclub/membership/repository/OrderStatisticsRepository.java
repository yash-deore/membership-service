package com.firstclub.membership.repository;

import com.firstclub.membership.entity.OrderStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * Repository interface for OrderStatistics entity
 */
@Repository
public interface OrderStatisticsRepository extends JpaRepository<OrderStatistics, Long> {

    /**
     * Find order statistics by member ID
     */
    Optional<OrderStatistics> findByMemberId(Long memberId);

    /**
     * Find order statistics by member ID with pessimistic locking
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderStatistics o WHERE o.member.id = :memberId")
    Optional<OrderStatistics> findByMemberIdWithLock(@Param("memberId") Long memberId);

    /**
     * Find order statistics that need monthly reset
     */
    @Query("SELECT o FROM OrderStatistics o WHERE o.monthlyStatsResetDate IS NULL OR " +
           "(MONTH(o.monthlyStatsResetDate) != MONTH(CURRENT_TIMESTAMP) OR YEAR(o.monthlyStatsResetDate) != YEAR(CURRENT_TIMESTAMP))")
    java.util.List<OrderStatistics> findNeedingMonthlyReset();
} 