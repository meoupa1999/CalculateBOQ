package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.Shift;
import org.springframework.data.jpa.domain.Specification;

public class ShiftSpecification {
    public static Specification<Shift> isActive() {
        return (root, query, cb) -> {
            return cb.isTrue(root.get("audit").get("isActive"));
        };
    }
}
