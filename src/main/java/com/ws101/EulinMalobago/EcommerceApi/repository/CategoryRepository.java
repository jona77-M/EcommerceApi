package com.ws101.EulinMalobago.EcommerceApi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ws101.EulinMalobago.EcommerceApi.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
}
