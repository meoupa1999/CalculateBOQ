package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.SpecialLocation;
import org.springframework.data.jpa.domain.Specification;

public class SpecialLocationSpecification {
    public static Specification<SpecialLocation> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("audit").get("isActive"));
    }

    public static Specification<SpecialLocation> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("locationName")), "%" + name.toLowerCase() + "%");
        };
    }
}
