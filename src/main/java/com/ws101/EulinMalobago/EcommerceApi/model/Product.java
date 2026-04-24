package com.ws101.EulinMalobago.EcommerceApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a catalog product stored in the database.
 *
 * Relationship notes:
 * Each product belongs to one {@link Category}, while one category can own many
 * products. The category relationship is loaded lazily to avoid fetching the
 * full category graph every time a product is queried.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required.")
    @Size(min = 2, message = "Product name must be at least 2 characters.")
    @Column(name = "product_name", nullable = false, length = 120)
    private String productName;

    @Column(length = 1000)
    private String description;

    @Positive(message = "Price must be a positive number.")
    @Column(nullable = false)
    private double price;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Min(value = 0, message = "Stock quantity must be non-negative.")
    @Column(nullable = false)
    private int stockQuantity;

    @Column(length = 500)
    private String imageUrl;

    @Transient
    private String categoryName;

    /**
     * Exposes the category name in API payloads without forcing clients to send a
     * nested category object.
     *
     * @return the resolved category name, or the transient fallback during request
     *         binding
     */
    public String getCategoryName() {
        if (categoryName != null) {
            return categoryName;
        }
        return category != null ? category.getName() : null;
    }
}
