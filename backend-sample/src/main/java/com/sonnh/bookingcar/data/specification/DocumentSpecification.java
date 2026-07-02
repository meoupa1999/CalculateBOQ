package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class DocumentSpecification {

    public static Specification<Document> hasOwnerType(DocumentOwnerType ownerType) {
        return (root, query, cb) -> ownerType == null ? null : cb.equal(root.get("ownerType"), ownerType);
    }

    public static Specification<Document> hasDocumentType(DocumentType documentType) {
        return (root, query, cb) -> documentType == null ? null : cb.equal(root.get("documentType"), documentType);
    }

    public static Specification<Document> hasSearchKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            Join<Document, User> userJoin = root.join("user", JoinType.LEFT);
            Join<Document, Vehicle> vehicleJoin = root.join("vehicle", JoinType.LEFT);

            Predicate documentNumberMatch = cb.like(cb.lower(root.get("documentNumber")), pattern);
            Predicate userNameMatch = cb.like(cb.lower(userJoin.get("fullName")), pattern);
            Predicate vehiclePlateMatch = cb.like(cb.lower(vehicleJoin.get("plateNumber")), pattern);

            return cb.or(documentNumberMatch, userNameMatch, vehiclePlateMatch);
        };
    }

    public static Specification<Document> hasStatus(DocumentStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            LocalDate now = LocalDate.now();
            switch (status) {
                case VALID:
                    return cb.greaterThan(root.get("expiredDate"), now.plusDays(90));
                case WARNING_3M:
                    return cb.and(
                            cb.greaterThan(root.get("expiredDate"), now.plusDays(30)),
                            cb.lessThanOrEqualTo(root.get("expiredDate"), now.plusDays(90))
                    );
                case WARNING_1M:
                    return cb.and(
                            cb.greaterThan(root.get("expiredDate"), now.plusDays(7)),
                            cb.lessThanOrEqualTo(root.get("expiredDate"), now.plusDays(30))
                    );
                case CRITICAL_7D:
                    return cb.and(
                            cb.greaterThanOrEqualTo(root.get("expiredDate"), now),
                            cb.lessThanOrEqualTo(root.get("expiredDate"), now.plusDays(7))
                    );
                case EXPIRED:
                    return cb.lessThan(root.get("expiredDate"), now);
                default:
                    return null;
            }
        };
    }
}
