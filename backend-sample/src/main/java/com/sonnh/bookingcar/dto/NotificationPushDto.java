package com.sonnh.bookingcar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPushDto {
    private UUID notificationId;

    private UUID requestId;

    private String title;

    private String message;

    /**
     * Type of notification:
     * - Admin types: BOOKING, VEHICLE, DRIVER, ALERT, REVENUE, INFO
     * - Tourist types: BOOKING, DRIVER, PROMOTION, REVIEW, PAYMENT, INFO
     */
    private String type;

    private LocalDateTime timestamp;

    private String actionUrl;

    private Map<String, Object> metadata;
}
