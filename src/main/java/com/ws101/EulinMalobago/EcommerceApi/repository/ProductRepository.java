package com.ws101.EulinMalobago.EcommerceApi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ws101.EulinMalobago.EcommerceApi.model.Product;

/**
 * Repository interface for {@link Product} database operations.
 * It provides built-in CRUD methods from {@link JpaRepository} and
 * custom finder methods used by the service layer.
 *
 * @author Eulin Malobago
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds products whose names contain the given text, ignoring letter case.
     *
     * @param productName text to search in the product name
     * @return matching products
     */
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    /**
     * Finds products whose category name contains the given text, ignoring letter
     * case.
     *
     * @param categoryName text to search in the category name
     * @return matching products
     */
    List<Product> findByCategory_NameContainingIgnoreCase(String categoryName);

    /**
     * Finds products with a price less than or equal to the given value.
     *
     * @param price maximum allowed price
     * @return matching products
     */
    List<Product> findByPriceLessThanEqual(double price);

    /**
     * Finds products with a price greater than or equal to the given value.
     *
     * @param price minimum allowed price
     * @return matching products
     */
    List<Product> findByPriceGreaterThanEqual(double price);

    /**
     * Finds products whose price is between the given minimum and maximum values.
     *
     * @param minPrice minimum price in the range
     * @param maxPrice maximum price in the range
     * @return matching products
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}
