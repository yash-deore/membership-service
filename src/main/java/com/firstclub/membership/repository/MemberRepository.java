package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Member entity
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find a member by userId
     */
    Optional<Member> findByUserId(String userId);

    /**
     * Find a member by email
     */
    Optional<Member> findByEmail(String email);

    /**
     * Check if a member exists by userId
     */
    boolean existsByUserId(String userId);

    /**
     * Check if a member exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find a member with active subscription
     */
    @Query("SELECT m FROM Member m JOIN m.subscriptions s WHERE s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP AND m.userId = :userId")
    Optional<Member> findByUserIdWithActiveSubscription(@Param("userId") String userId);

    /**
     * Find members by cohort
     */
    @Query("SELECT m FROM Member m WHERE m.cohort = :cohort AND m.active = true")
    java.util.List<Member> findByCohort(@Param("cohort") String cohort);
} 