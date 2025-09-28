package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.MembershipPlanType;
import com.firstclub.membership.enums.MembershipTierType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for subscribing to a membership plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Plan type is required")
    private MembershipPlanType planType;

    @NotNull(message = "Tier type is required")
    private MembershipTierType tierType;

    private Boolean autoRenew = true;
} 