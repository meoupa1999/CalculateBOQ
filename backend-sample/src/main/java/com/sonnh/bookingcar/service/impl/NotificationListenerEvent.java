package com.sonnh.bookingcar.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sonnh.bookingcar.dto.response.admin.AdminNotificationEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListenerEvent {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAdminNotificationEvent(AdminNotificationEvent event) {
        messagingTemplate.convertAndSend("/topic/admin/notification", event.getNotificationPushDto());
    }
}
