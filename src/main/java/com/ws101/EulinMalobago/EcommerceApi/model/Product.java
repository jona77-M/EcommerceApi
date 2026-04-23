package com.ws101.EulinMalobago.EcommerceApi.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a product in the e-commerce catalog.
 * eulin and malobago are the authors of this code.
 * This class includes fields for product details such as name, description, price, category, stock quantity, and image URL.
 * It uses Lombok annotations to generate boilerplate code for getters, setters, toString, equals, and hashCode methods, as well as constructors.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	private Long id;

	@NotBlank(message = "Product name is required.")
	@Size(min = 2, message = "Product name must be at least 2 characters.")
	private String productName;

	private String description;

	@Positive(message = "Price must be a positive number.")
	private double price;

	@NotBlank(message = "Category is required.")
	private String category;

	@Min(value = 0, message = "Stock quantity must be non-negative.")
	private int stockQuantity;

	private String imageUrl;
}