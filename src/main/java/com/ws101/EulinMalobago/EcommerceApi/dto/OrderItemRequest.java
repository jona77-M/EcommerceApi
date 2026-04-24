package com.ws101.EulinMalobago.EcommerceApi.dto;

/**
 * @author Eulin Malobago
 * this orderitemRequest
 */

public class OrderItemRequest {
    private Long productId;
    private int quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
