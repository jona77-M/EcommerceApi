package com.ws101.EulinMalobago.EcommerceApi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ws101.EulinMalobago.EcommerceApi.model.Category;

/**
 * Repository interface for {@link Category} database operations.
 * It provides built-in CRUD methods from {@link JpaRepository} and
 * custom lookup methods for category records.
 *
 * @author Eulin Malobago
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Finds a category by name, ignoring letter case.
     *
     * @param name category name to search for
     * @return the matching category, if found
     */
    Optional<Category> findByNameIgnoreCase(String name);
}
