package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.MembershipTierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for membership tier information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTierDto {

    private Long id;
    private MembershipTierType tierType;
    private String name;
    private String description;
    private Integer level;
    private List<BenefitDto> benefits;
    private Boolean isAvailable;
} 