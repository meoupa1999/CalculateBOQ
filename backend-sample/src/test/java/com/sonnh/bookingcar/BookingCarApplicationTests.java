package com.sonnh.bookingcar;

import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
class BookingCarApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Rollback(false)
    void updateDriverPassword() {
        // ID của driver bạn muốn update
        UUID userId = UUID.fromString("68134fe6-287b-453f-a709-45719e4c35f1");
        String rawPassword = "123456";

        userRepository.findById(userId).ifPresent(user -> {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);

            System.out.println("########## Cập nhật mật khẩu thành công cho: " + user.getUsername() + " ##########");
        });
    }

    @Test
    @Rollback(false)
    void check() {
        System.out.println("My Check " + serviceRequestRepository.countByDriverIdAndStatus(
                UUID.fromString("68134fe6-287b-453f-a709-45719e4c35f1"), BookingStatus.RUNNING));
    }

}
