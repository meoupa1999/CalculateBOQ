package com.sonnh.bookingcar.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

/**
 * Utility class for security operations.
 * Help developers to get current logged-in user information.
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the ID of the current logged-in user.
     * @return UUID of the user or null if not authenticated.
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getId();
        }

        return null;
    }

    /**
     * Get the username of the current logged-in user.
     * @return String username or null if not authenticated.
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }
}
