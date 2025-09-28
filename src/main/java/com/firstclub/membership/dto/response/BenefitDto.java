package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.BenefitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for benefit information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitDto {

    private Long id;
    private BenefitType benefitType;
    private String name;
    private String description;
    private String value;
    private String applicableCategories;
    private Integer displayOrder;
} 