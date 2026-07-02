package com.sonnh.elv.data.specification;

import com.sonnh.elv.data.domain.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {

    public static Specification<Project> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("audit").get("isActive"));
    }

    public static Specification<Project> search(String term) {
        return (root, query, cb) -> {
            if (term == null || term.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + term.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
