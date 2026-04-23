package com.ws101.EulinMalobago.EcommerceApi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.ws101.EulinMalobago.EcommerceApi.model.Product;

/**
 * Service class for product-related operations.
 *
 * Provides business logic for filtering, searching, and managing products using
 * an in-memory list instead of a database.
 *
 * @author R. Malobago & E. Eulin - WS101 - BSIT-2B
 * @see Product
 */
@Service
public class ProductService {
	private final List<Product> productList = new ArrayList<>();
	private final AtomicLong nextId = new AtomicLong(1);

	/**
	 * Creates the service and loads sample product data into memory.
	 */
	public ProductService() {
		addSampleProduct("Wireless Mouse", "Ergonomic wireless mouse with USB receiver.", 599.00, "Accessories", 35,
				"/images/wireless.jpg");
		addSampleProduct("Mechanical Keyboard", "Compact blue-switch mechanical keyboard.", 2499.00, "Accessories", 18,
				"/images/Mechanical%20Keyboard.jpg");
		addSampleProduct("USB-C Hub", "Seven-in-one USB-C hub with HDMI and card reader.", 1399.00, "Accessories", 22,
				"/images/charger1.jpg");
		addSampleProduct("Gaming Headset", "Over-ear headset with noise-cancelling microphone.", 1799.00, "Audio", 14,
				"/images/Gaming%20Mouse.jpg");
		addSampleProduct("Bluewow Phone Cooler",
				"Portable semiconductor phone cooler with RGB lighting, low-noise fan, and adjustable clip.",
				1299.00, "Mobile", 27,
				"/images/bluewow.jpg");
		addSampleProduct("Smart Watch", "Fitness smartwatch with heart-rate tracking.", 3299.00, "Wearables", 12,
				"/images/smart_watch.jpg");
		addSampleProduct("Laptop Stand", "Adjustable aluminum laptop stand.", 899.00, "Office", 40,
				"/images/laptop-stand.jpg");
		addSampleProduct("Desk Lamp", "LED desk lamp with brightness controls.", 749.00, "Office", 31,
				"/images/office-tab.jpg");
		addSampleProduct("Phone Charger", "Fast-charging USB-C wall charger.", 499.00, "Mobile", 50,
				"/images/charger.jpg");
		addSampleProduct("Power Bank", "10000mAh portable power bank.", 1099.00, "Mobile", 24,
				"/images/powerbank.jpg");
		addSampleProduct("Airpod", "True wireless earbuds with charging case.", 1999.00, "Audio", 15,
				"/images/airpod.jpg");
	}

	/**
	 * Retrieves all products.
	 *
	 * @return a list containing all products
	 */
	public synchronized List<Product> getAllProducts() {
		return new ArrayList<>(productList);
	}

	/**
	 * Finds a product by its ID.
	 *
	 * @param id the product ID to find
	 * @return the matching product
	 * @throws ProductNotFoundException if no product has the requested ID
	 */
	public synchronized Product getProductById(Long id) {
		return productList.stream()
				.filter(product -> product.getId().equals(id))
				.findFirst()
				.orElse(null);
	}

	

	/**
	 * Replaces all editable fields for an existing product.
	 *
	 * @param id      the product ID to update
	 * @param product the replacement product data
	 * @return the updated product
	 * @throws ProductNotFoundException if no product has the requested ID
	 * @throws IllegalArgumentException if the product data is invalid
	 */
	public synchronized Product updateProduct(Long id, Product product) {
		validateProduct(product);
		Product existingProduct = getProductById(id);
		existingProduct.setProductName(product.getProductName());
		existingProduct.setDescription(product.getDescription());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setCategory(product.getCategory());
		existingProduct.setStockQuantity(product.getStockQuantity());
		existingProduct.setImageUrl(product.getImageUrl());
		return existingProduct;
	}

	/**
	 * Partially updates an existing product.
	 *
	 * @param id      the product ID to update
	 * @param changes the field changes to apply
	 * @return the updated product
	 * @throws ProductNotFoundException if no product has the requested ID
	 * @throws IllegalArgumentException if the patch field or value is invalid
	 */
	public synchronized Product patchProduct(Long id, Map<String, Object> changes) {
		Product existingProduct = getProductById(id);

		if (changes == null || changes.isEmpty()) {
			throw new IllegalArgumentException("At least one product field is required for PATCH.");
		}

		for (Map.Entry<String, Object> change : changes.entrySet()) {
			switch (change.getKey()) {
				case "name" -> existingProduct.setProductName(asString(change.getValue(), "name"));
				case "description" -> existingProduct.setDescription(asString(change.getValue(), "description"));
				case "price" -> existingProduct.setPrice(asDouble(change.getValue(), "price"));
				case "category" -> existingProduct.setCategory(asString(change.getValue(), "category"));
				case "stockQuantity" -> existingProduct.setStockQuantity(asInteger(change.getValue(), "stockQuantity"));
				case "imageUrl" -> existingProduct.setImageUrl(asString(change.getValue(), "imageUrl"));
				case "id" -> throw new IllegalArgumentException("Product ID cannot be changed.");
				default -> throw new IllegalArgumentException("Unsupported product field: " + change.getKey());
			}
		}

		validateProduct(existingProduct);
		return existingProduct;
	}

	/**
	 * Deletes a product by ID.
	 *
	 * @param id the product ID to delete
	 */
	public synchronized void deleteProduct(Long id) {
		Product product = getProductById(id);
		productList.remove(product);
	}

	/**
	 * Filters products by a selected criteria.
	 *
	 * @param filterType  the field to filter by: name, category, price, minPrice,
	 *                    or maxPrice
	 * @param filterValue the value used for filtering
	 * @return the matching products
	 * @throws IllegalArgumentException if the filter type or value is invalid
	 */
	public synchronized List<Product> filterProducts(String filterType, String filterValue) {
		if (isBlank(filterType) || isBlank(filterValue)) {
			throw new IllegalArgumentException("filterType and filterValue are required.");
		}

		String normalizedType = filterType.trim().toLowerCase(Locale.ROOT);
		String normalizedValue = filterValue.trim().toLowerCase(Locale.ROOT);

		return switch (normalizedType) {
			case "name" -> productList.stream()
					.filter(product -> product.getProductName().toLowerCase(Locale.ROOT).contains(normalizedValue))
					.toList();
			case "category" -> productList.stream()
					.filter(product -> product.getCategory().toLowerCase(Locale.ROOT).contains(normalizedValue))
					.toList();
			case "price" -> filterProductWithMaxPrice(parsePrice(filterValue));
			case "minprice" -> filterProductWithMinPrice(parsePrice(filterValue));
			case "maxprice" -> filterProductWithMaxPrice(parsePrice(filterValue));
			default -> throw new IllegalArgumentException(
					"Unsupported filterType. Use name, category, price, minPrice, or maxPrice.");
		};
	}

	/**
	 * Filters products with prices greater than or equal to the minimum price.
	 *
	 * @param minPrice the minimum price threshold
	 * @return products with prices greater than or equal to minPrice
	 * @throws IllegalArgumentException if minPrice is negative
	 */
	public synchronized List<Product> filterProductWithMinPrice(double minPrice) {
		if (minPrice < 0) {
			throw new IllegalArgumentException("Minimum price must be non-negative.");
		}
		return productList.stream()
				.filter(product -> product.getPrice() >= minPrice)
				.toList();
	}

	/**
	 * Filters products with prices less than or equal to the maximum price.
	 *
	 * @param maxPrice the maximum price threshold
	 * @return products with prices less than or equal to maxPrice
	 * @throws IllegalArgumentException if maxPrice is negative
	 */
	public synchronized List<Product> filterProductWithMaxPrice(double maxPrice) {
		if (maxPrice < 0) {
			throw new IllegalArgumentException("Maximum price must be non-negative.");
		}
		return productList.stream()
				.filter(product -> product.getPrice() <= maxPrice)
				.toList();
	}

	private void addSampleProduct(String name, String description, double price, String category, int stockQuantity,
			String imageUrl) {
		productList.add(new Product(nextId.getAndIncrement(), name, description, price, category, stockQuantity,
				imageUrl));
	}

	private void validateProduct(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("Product data is required.");
		}
		if (isBlank(product.getProductName()) || product.getProductName().trim().length() < 2) {
			throw new IllegalArgumentException("Product name is required and must contain at least 2 characters.");
		}
		if (product.getPrice() <= 0) {
			throw new IllegalArgumentException("Product price must be a positive number.");
		}
		if (isBlank(product.getCategory())) {
			throw new IllegalArgumentException("Product category is required.");
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
}