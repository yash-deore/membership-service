package com.firstclub.membership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for order statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatisticsDto {

    private Integer totalOrderCount;
    private Integer monthlyOrderCount;
    private BigDecimal totalOrderValue;
    private BigDecimal monthlyOrderValue;
    private BigDecimal averageOrderValue;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
} 