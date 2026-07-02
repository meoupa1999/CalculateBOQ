package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.dto.NotificationPushDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationEvent {
    private NotificationPushDto notificationPushDto;
}
