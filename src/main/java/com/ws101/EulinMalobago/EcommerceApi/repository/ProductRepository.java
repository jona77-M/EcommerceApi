package com.ws101.EulinMalobago.EcommerceApi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ws101.EulinMalobago.EcommerceApi.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    List<Product> findByCategory_NameContainingIgnoreCase(String categoryName);

    List<Product> findByPriceLessThanEqual(double price);

    List<Product> findByPriceGreaterThanEqual(double price);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
