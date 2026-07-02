package com.sonnh.bookingcar.data.specification;

import com.sonnh.bookingcar.data.domain.Vehicle;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import java.util.UUID;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;

public class VehicleSpecification {
    public static Specification<Vehicle> hasType(String type) {
        return (root, query, cb) -> {
            if (type == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Vehicle> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("model")), pattern),
                cb.like(cb.lower(root.get("plateNumber")), pattern)
            );
        };
    }

    public static Specification<Vehicle> isActive() {
        return (root, query, cb) -> {
            return cb.isTrue(root.get("audit").get("isActive"));
        };
    }

    public static Specification<Vehicle> hasMissingDocuments() {
        return (root, query, cb) -> {
            // Subquery for Registration
            Subquery<UUID> regSub = query.subquery(UUID.class);
            Root<Document> regRoot = regSub.from(Document.class);
            regSub.select(regRoot.get("id")).where(
                cb.equal(regRoot.get("vehicle"), root),
                cb.equal(regRoot.get("documentType"), DocumentType.REGISTRATION)
            );

            // Subquery for Badge
            Subquery<UUID> badgeSub = query.subquery(UUID.class);
            Root<Document> badgeRoot = badgeSub.from(Document.class);
            badgeSub.select(badgeRoot.get("id")).where(
                cb.equal(badgeRoot.get("vehicle"), root),
                cb.equal(badgeRoot.get("documentType"), DocumentType.BADGE)
            );

            // Subquery for Insurance
            Subquery<UUID> insSub = query.subquery(UUID.class);
            Root<Document> insRoot = insSub.from(Document.class);
            insSub.select(insRoot.get("id")).where(
                cb.equal(insRoot.get("vehicle"), root),
                cb.equal(insRoot.get("documentType"), DocumentType.MANDATORY_INSURANCE)
            );

            return cb.or(
                cb.not(cb.exists(regSub)),
                cb.not(cb.exists(badgeSub)),
                cb.not(cb.exists(insSub))
            );
        };
    }
}
