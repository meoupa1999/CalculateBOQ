package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import org.springframework.data.jpa.domain.Specification;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.AirportTransferDetail;
import com.sonnh.bookingcar.data.domain.TourBookingDetail;
import com.sonnh.bookingcar.data.domain.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class ServiceRequestSpecification {
    public static Specification<ServiceRequest> hasType(ServiceType type) {
        return (root, query, cb) -> {
            if (type == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<ServiceRequest> hasStatus(BookingStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<ServiceRequest> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.toLowerCase() + "%";
            
            // Note: Use LEFT joins to ensure we don't filter out requests that don't have one type of detail
            // root.get("nested") often results in an INNER JOIN by default in Specifications
            Join<ServiceRequest, AirportTransferDetail> airportJoin = root.join("airportTransferDetail", JoinType.LEFT);
            Join<ServiceRequest, TourBookingDetail> tourJoin = root.join("tourBookingDetail", JoinType.LEFT);
            Join<ServiceRequest, User> touristJoin = root.join("tourist", JoinType.INNER);

            return cb.or(
                cb.like(cb.lower(root.get("bookingCode")), pattern),
                cb.like(cb.lower(touristJoin.get("fullName")), pattern),
                cb.like(cb.lower(airportJoin.get("pickupLocation")), pattern),
                cb.like(cb.lower(tourJoin.get("pickupLocation")), pattern)
            );
        };
    }

    public static Specification<ServiceRequest> isActive() {
        return (root, query, cb) -> {
            return cb.isTrue(root.get("audit").get("isActive"));
        };
    }

    public static Specification<ServiceRequest> hasTourist(User tourist) {
        return (root, query, cb) -> {
            if (tourist == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("tourist"), tourist);
        };
    }
}
