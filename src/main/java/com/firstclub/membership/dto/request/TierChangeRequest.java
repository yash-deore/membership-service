package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.MembershipTierType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for upgrading or downgrading membership tier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierChangeRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Target tier type is required")
    private MembershipTierType targetTierType;
} 