package com.ws101.EulinMalobago.EcommerceApi.model;

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
	private String productName;
	private String description;
	private double price;
	private String category;
	private int stockQuantity;
	private String imageUrl;
}