package com.sonnh.elv.data.repository;

import com.sonnh.elv.data.domain.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentIsNullOrderByOrderIndexAsc();
}
