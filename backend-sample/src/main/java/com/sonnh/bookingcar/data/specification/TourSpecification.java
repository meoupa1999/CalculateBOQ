package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.Tour;
import org.springframework.data.jpa.domain.Specification;

public class TourSpecification {
    public static Specification<Tour> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("audit").get("isActive"));
    }

    public static Specification<Tour> hasNameLike(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
        };
    }
}
