package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.VehicleType;
import org.springframework.data.jpa.domain.Specification;

public class VehicleTypeSpecification {
    public static Specification<VehicleType> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("name").as(String.class)), pattern)
            );
        };
    }

    public static Specification<VehicleType> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("audit").get("isActive"));
    }
}
