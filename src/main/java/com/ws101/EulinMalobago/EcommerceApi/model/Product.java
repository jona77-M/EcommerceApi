package com.ws101.EulinMalobago.EcommerceApi.model;

public class Product {
    private Long id;
	private String name;
	private String description;
	private double price;
	private String category;
	private int stockQuantity;
	private String imageUrl;

	/**
	 * Creates an empty product instance for JSON request binding.
     * @author R. Malobago & E. Eulin - WS101 - BSIT-2B
     * by sir Tomas Paolo Echaluce
	 */
	public Product() {
	}

	/**
	 * Creates a product with all catalog fields.
	 *
	 * @param id the unique product identifier
	 * @param name the product name
	 * @param description the product description
	 * @param price the product price
	 * @param category the product category
	 * @param stockQuantity the available stock quantity
	 * @param imageUrl an optional product image URL
	 */
	public Product(Long id, String name, String description, double price, String category, int stockQuantity,
			String imageUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.stockQuantity = stockQuantity;
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
