package com.sonnh.bookingcar.data;

import com.sonnh.bookingcar.data.domain.Role;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.repository.RoleRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("########## USER SEEDER STARTING ##########");
        log.info("Checking/Seeding default users...");

        // Ensure Roles exist
        Role adminRole = ensureRole("ADMIN");
        Role driverRole = ensureRole("DRIVER");
        Role touristRole = ensureRole("TOURIST");

        // Seed Admin
        ensureUser("admin", "admin@example.com", "123456", "System Admin", "0000000000", adminRole);

        // Seed Driver
        ensureUser("driver1", "driver1@example.com", "123456", "Test Driver 1", "0123456789", driverRole);

        // Seed Tourist
        ensureUser("tourist1", "tourist1@example.com", "123456", "Test Tourist 1", "0987654321", touristRole);

        System.out.println("########## USER SEEDER COMPLETED ##########");
        log.info("User seeding completed.");
    }

    private Role ensureRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    log.info("Creating role: {}", roleName);
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    return roleRepository.save(role);
                });
    }

    private void ensureUser(String username, String email, String password, String fullName, String phone, Role role) {
        userRepository.findByUsername(username).ifPresentOrElse(
            user -> {
                log.info("User {} already exists. Updating password to ensure sync.", username);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole(role); // Ensure role is correct
                userRepository.save(user);
            },
            () -> {
                log.info("Creating user: {}", username);
                User user = User.builder()
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .fullName(fullName)
                        .phone(phone)
                        .role(role)
                        .build();
                userRepository.save(user);
            }
        );
    }
}
