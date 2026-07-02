package com.sonnh.elv.data.specification;

import com.sonnh.elv.data.domain.Tower;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TowerSpecification {

    public static Specification<Tower> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("audit").get("isActive"));
    }

    public static Specification<Tower> hasProjectId(UUID projectId) {
        return (root, query, cb) -> {
            if (projectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("project").get("id"), projectId);
        };
    }

    public static Specification<Tower> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("specialName")), pattern)
            );
        };
    }
}
