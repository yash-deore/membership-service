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
} 