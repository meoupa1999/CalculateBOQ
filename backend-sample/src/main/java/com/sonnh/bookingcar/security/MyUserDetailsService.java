package com.sonnh.bookingcar.security;

import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try finding by username first
        User user = userRepository.findByUsername(identifier).orElse(null);
        boolean isLoginByPhone = false;

        // If not found, try finding by phone
        if (user == null) {
            user = userRepository.findByPhone(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));
            isLoginByPhone = true;
        }

        String roleName = user.getRole() != null ? user.getRole().getName() : "";

        // Role-based login method validation
        if (isLoginByPhone) {
            if ("ADMIN".equals(roleName) || "DRIVER".equals(roleName)) {
                throw new DisabledException("Admin và Driver không được phép đăng nhập bằng số điện thoại");
            }
        } else {
            if ("TOURIST".equals(roleName)) {
                throw new DisabledException("Tourist chỉ được phép đăng nhập bằng số điện thoại");
            }
        }

        if ("DRIVER".equals(roleName)) {
            if (user.getDriverStatus() == DriverStatus.PENDING_APPROVAL) {
                throw new DisabledException("Tài khoản của bạn đang chờ quản trị viên phê duyệt");
            }
            if (user.getDriverStatus() == DriverStatus.REJECTED) {
                String message = "Hồ sơ đăng ký của bạn đã bị từ chối \n";
                if (user.getNotes() != null && !user.getNotes().isBlank()) {
                    message += "Lý do: " + user.getNotes();
                } else {
                    message += "Vui lòng liên hệ hỗ trợ";
                }
                throw new DisabledException(message);
            }
        }
        if (!user.getAudit().getIsActive()) {
            throw new DisabledException("Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt");
        }

        return new MyUserDetails(user);
    }
}
