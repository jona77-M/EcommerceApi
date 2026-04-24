package com.ws101.EulinMalobago.EcommerceApi.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ws101.EulinMalobago.EcommerceApi.model.Product;
import com.ws101.EulinMalobago.EcommerceApi.service.ProductService;

/**
 * REST controller that exposes product catalog API endpoints. eulin and
 * 
 * @author malobago are the authors of this code. This controller provides
 *         endpoints for
 *         CRUD operations on products, as well as filtering products by various
 *         criteria. It uses a ProductService to perform business logic and data
 *         access
 *         operations, and returns appropriate HTTP responses for each endpoint.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a product controller with the required service dependency.
     *
     * @param productService the service used for product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products.
     *
     * @return a 200 response containing all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieves a product by ID.
     *
     * @param id the product ID to retrieve
     * @return a 200 response containing the product
     */
    @GetMapping("/{id}")
    // Lab 4.3: @PathVariable extracts the product ID from the URL segment.
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Filters products by the provided type and value.
     *
     * @param filterType  the filter field: name, category, price, minPrice, or
     *                    maxPrice
     * @param filterValue the value to filter with
     * @return a 200 response containing matching products
     */
    @GetMapping("/filter")
    // Lab 4.3: @RequestParam reads filterType and filterValue from the query
    // string.
    public ResponseEntity<List<Product>> filterProducts(@RequestParam String filterType,
            @RequestParam String filterValue) {
        return ResponseEntity.ok(productService.filterProducts(filterType, filterValue));
    }

    /**
     * Creates a new product.
     *
     * @param product the product data from the request body
     * @return a 201 response containing the created product and Location header
     */
    @PostMapping
    // Lab 4.3: @RequestBody receives JSON data and ResponseEntity controls status
    // and headers.
    // public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    // Product createdProduct = productService.createProduct(product);
    // return ResponseEntity.created(URI.create("/api/v1/products/" +
    // createdProduct.getId())).body(createdProduct);
    // }

    // @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + createdProduct.getId()))
                .body(createdProduct);
    }

    /**
     * Replaces an existing product.
     *
     * @param id      the product ID to update
     * @param product the replacement product data
     * @return a 200 response containing the updated product
     */
    @PutMapping("/{id}")
    // Lab 4.3: uses @PathVariable for the ID and @RequestBody for the full JSON
    // replacement.
    // public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
    //     return ResponseEntity.ok(productService.updateProduct(id, product));
    // }

    // @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    /**
     * Partially updates an existing product.
     *
     * @param id      the product ID to update
     * @param changes the partial product data to apply
     * @return a 200 response containing the updated product
     */
    @PatchMapping("/{id}")
    // Lab 4.3: @RequestBody accepts partial JSON fields for PATCH updates.
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Map<String, Object> changes) {
        return ResponseEntity.ok(productService.patchProduct(id, changes));
    }

    /**
     * Deletes an existing product.
     *
     * @param id the product ID to delete
     * @return a 204 response with no response body
     */
    @DeleteMapping("/{id}")
    // Lab 4.3: ResponseEntity returns an explicit 204 No Content response.
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
