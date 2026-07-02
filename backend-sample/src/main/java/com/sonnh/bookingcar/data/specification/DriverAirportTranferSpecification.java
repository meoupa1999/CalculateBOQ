package com.sonnh.bookingcar.data.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import com.sonnh.bookingcar.data.domain.AirportTransferDetail;
import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class DriverAirportTranferSpecification {

    public static Specification<ServiceRequest> hasStatusIn(Set<BookingStatus> statuses) {
        return (root, query, cb) -> {

            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    public static Specification<ServiceRequest> inTimeRange(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return (root, query, cb) -> {

            // query.distinct(true);

            Join<ServiceRequest, AirportTransferDetail> detail = root
                    .join("airportTransferDetail", JoinType.INNER);

            if (startDate == null && endDate == null) {
                return cb.conjunction();
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null && endDate == null) {
                LocalDate d = startDate.toLocalDate();
                start = d.atStartOfDay();
                end = d.plusDays(1).atStartOfDay().minusNanos(1);
            } else if (startDate != null) {
                start = startDate.toLocalDate().atStartOfDay();
                end = endDate.toLocalDate().plusDays(1).atStartOfDay().minusNanos(1);
            }
            // else {
            // LocalDate d = endDate.toLocalDate();
            // start = d.atStartOfDay();
            // end = d.plusDays(1).atStartOfDay().minusNanos(1);
            // }

            return cb.and(
                    cb.greaterThanOrEqualTo(detail.get("pickupDate"), start),
                    cb.lessThanOrEqualTo(detail.get("pickupDate"), end));
        };
    }

    public static Specification<ServiceRequest> hasDriverId(UUID driverId) {
        return (root, query, cb) -> {

            if (driverId == null) {
                return cb.conjunction();
            }

            Join<ServiceRequest, AirportTransferDetail> detail = root
                    .join("airportTransferDetail", JoinType.INNER);

            Join<AirportTransferDetail, Shift> shift = detail.join("currentShift",
                    JoinType.INNER);

            Join<Shift, User> driver = shift.join("driver", JoinType.INNER);

            return cb.equal(driver.get("id"), driverId);
        };
    }

    public static Specification<ServiceRequest> distinct() {
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };
    }
}
