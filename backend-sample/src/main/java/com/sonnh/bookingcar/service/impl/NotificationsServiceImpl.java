package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Notification;
import com.sonnh.bookingcar.data.repository.NotificationRepository;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.security.SecurityUtils;
import com.sonnh.bookingcar.service.interfaces.NotificationsService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationsServiceImpl implements NotificationsService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // @Override
    // @Transactional
    // public void markAllAsRead() {
    // String username = SecurityUtils.getCurrentUserUsername();
    // if (username == null) {
    // throw new IllegalArgumentException("No user logged in");
    // }
    // notificationRepository.markAllAsRead(username);
    // }
}
