package com.ws101.EulinMalobago.EcommerceApi.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ws101.EulinMalobago.EcommerceApi.model.Category;
import com.ws101.EulinMalobago.EcommerceApi.model.Product;
import com.ws101.EulinMalobago.EcommerceApi.repository.CategoryRepository;
import com.ws101.EulinMalobago.EcommerceApi.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

/**
 * Service class for product-related operations backed by Spring Data JPA.
 *
 * @author Eulin Malobago
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Validator validator;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, Validator validator) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.validator = validator;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return all persisted products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(this::hydrateCategoryName);
        return products;
    }

    /**
     * Finds a product by its database identifier.
     *
     * @param id product id
     * @return the matching product
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
        hydrateCategoryName(product);
        return product;
    }

    /**
     * Creates and persists a new product.
     *
     * @param product inbound product payload
     * @return persisted product
     */
    @Transactional
    public Product createProduct(@Valid Product product) {
        validateProduct(product);
        product.setId(null);
        product.setCategory(resolveCategory(product));
        Product savedProduct = productRepository.save(product);
        hydrateCategoryName(savedProduct);
        return savedProduct;
    }

    /**
     * Replaces all editable fields for an existing product.
     *
     * @param id product id
     * @param product replacement payload
     * @return updated product
     */
    @Transactional
    public Product updateProduct(Long id, @Valid Product product) {
        validateProduct(product);
        Product existingProduct = getProductById(id);
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(resolveCategory(product));
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setImageUrl(product.getImageUrl());
        Product savedProduct = productRepository.save(existingProduct);
        hydrateCategoryName(savedProduct);
        return savedProduct;
    }

    /**
     * Applies a partial update to an existing product.
     *
     * @param id product id
     * @param changes requested field updates
     * @return updated product
     */
    @Transactional
    public Product patchProduct(Long id, Map<String, Object> changes) {
        Product existingProduct = getProductById(id);

        if (changes == null || changes.isEmpty()) {
            throw new IllegalArgumentException("At least one product field is required for PATCH.");
        }

        Map<String, Object> normalizedChanges = new LinkedHashMap<>();
        for (Map.Entry<String, Object> change : changes.entrySet()) {
            String key = change.getKey();
            String normalizedKey = "name".equals(key) ? "productName" : key;
            normalizedChanges.put(normalizedKey, change.getValue());
        }

        for (Map.Entry<String, Object> change : normalizedChanges.entrySet()) {
            switch (change.getKey()) {
                case "productName" -> existingProduct.setProductName(asString(change.getValue(), "productName"));
                case "description" -> existingProduct.setDescription(asString(change.getValue(), "description"));
                case "price" -> existingProduct.setPrice(asDouble(change.getValue(), "price"));
                case "category", "categoryName" -> existingProduct.setCategory(resolveCategoryName(change.getValue()));
                case "stockQuantity" -> existingProduct.setStockQuantity(asInteger(change.getValue(), "stockQuantity"));
                case "imageUrl" -> existingProduct.setImageUrl(asString(change.getValue(), "imageUrl"));
                case "id" -> throw new IllegalArgumentException("Product ID cannot be changed.");
                default -> throw new IllegalArgumentException("Unsupported product field: " + change.getKey());
            }
        }

        validateProduct(existingProduct);
        Product savedProduct = productRepository.save(existingProduct);
        hydrateCategoryName(savedProduct);
        return savedProduct;
    }

    /**
     * Deletes a product by id.
     *
     * @param id product id
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Filters products using database-backed repository queries.
     *
     * @param filterType field to filter by
     * @param filterValue filter value
     * @return matching products
     */
    @Transactional(readOnly = true)
    public List<Product> filterProducts(String filterType, String filterValue) {
        if (isBlank(filterType) || isBlank(filterValue)) {
            throw new IllegalArgumentException("filterType and filterValue are required.");
        }

        String normalizedType = filterType.trim().toLowerCase(Locale.ROOT);

        List<Product> products = switch (normalizedType) {
            case "name" -> productRepository.findByProductNameContainingIgnoreCase(filterValue.trim());
            case "category" -> productRepository.findByCategory_NameContainingIgnoreCase(filterValue.trim());
            case "price", "maxprice" -> productRepository.findByPriceLessThanEqual(parsePrice(filterValue));
            case "minprice" -> productRepository.findByPriceGreaterThanEqual(parsePrice(filterValue));
            default -> throw new IllegalArgumentException(
                    "Unsupported filterType. Use name, category, price, minPrice, or maxPrice.");
        };
        products.forEach(this::hydrateCategoryName);
        return products;
    }

    /**
     * Finds products within a database price range.
     *
     * @param min minimum price
     * @param max maximum price
     * @return matching products
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsInPriceRange(double min, double max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Price range values must be non-negative.");
        }
        if (min > max) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price.");
        }
        List<Product> products = productRepository.findProductsInPriceRange(min, max);
        products.forEach(this::hydrateCategoryName);
        return products;
    }

    private Category resolveCategory(Product product) {
        if (product.getCategory() != null && !isBlank(product.getCategory().getName())) {
            return resolveCategoryName(product.getCategory().getName());
        }
        if (!isBlank(product.getCategoryName())) {
            return resolveCategoryName(product.getCategoryName());
        }
        throw new IllegalArgumentException("Category is required.");
    }

    private Category resolveCategoryName(Object value) {
        String categoryName = asString(value, "category");
        if (isBlank(categoryName)) {
            throw new IllegalArgumentException("Category is required.");
        }
        return categoryRepository.findByNameIgnoreCase(categoryName.trim())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName.trim());
                    return categoryRepository.save(newCategory);
                });
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product data is required.");
        }

        var violations = validator.validate(product);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            throw new IllegalArgumentException(message);
        }

        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Product stock quantity must be non-negative.");
        }
    }

    private double parsePrice(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Price filter value must be a valid number.");
        }
    }

    private String asString(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String stringValue)) {
            throw new IllegalArgumentException(fieldName + " must be a string.");
        }
        return stringValue;
    }

    private double asDouble(Object value, String fieldName) {
        if (!(value instanceof Number numberValue)) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
        return numberValue.doubleValue();
    }

    private int asInteger(Object value, String fieldName) {
        if (!(value instanceof Number numberValue)) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
        double doubleValue = numberValue.doubleValue();
        int intValue = numberValue.intValue();
        if (doubleValue != intValue) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
        return intValue;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void hydrateCategoryName(Product product) {
        if (product != null && product.getCategory() != null) {
            product.setCategoryName(product.getCategory().getName());
        }
    }
}
