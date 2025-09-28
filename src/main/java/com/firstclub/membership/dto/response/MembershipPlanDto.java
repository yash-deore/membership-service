package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.MembershipPlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for membership plan information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlanDto {

    private Long id;
    private MembershipPlanType planType;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer durationInMonths;
    private BigDecimal monthlyPrice;
    private BigDecimal discountPercentage;
    private Boolean isAvailable;
} 