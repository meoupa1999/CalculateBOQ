package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collection;

public class UserSpecification {
    public static Specification<User> hasRoleName(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("role").get("name"), roleName);
        };
    }

    public static Specification<User> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern));
        };
    }

    public static Specification<User> isActive() {
        return (root, query, cb) -> {
            return cb.isTrue(root.get("audit").get("isActive"));
        };
    }

    public static Specification<User> hasDriverStatus(DriverStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("driverStatus"), status);
        };
    }

    public static Specification<User> hasDriverStatus(Collection<DriverStatus> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("driverStatus").in(statuses);
        };
    }
}
