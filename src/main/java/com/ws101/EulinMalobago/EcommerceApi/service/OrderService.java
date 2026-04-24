package com.ws101.EulinMalobago.EcommerceApi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ws101.EulinMalobago.EcommerceApi.dto.OrderItemRequest;
import com.ws101.EulinMalobago.EcommerceApi.dto.OrderRequest;
import com.ws101.EulinMalobago.EcommerceApi.model.CustomerOrder;
import com.ws101.EulinMalobago.EcommerceApi.model.OrderItem;
import com.ws101.EulinMalobago.EcommerceApi.model.Product;
import com.ws101.EulinMalobago.EcommerceApi.repository.CustomerOrderRepository;
import com.ws101.EulinMalobago.EcommerceApi.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    public OrderService(CustomerOrderRepository customerOrderRepository, ProductRepository productRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CustomerOrder createOrder(OrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(request.getCustomerName());

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            order.getOrderItems().add(orderItem);
        }

        return customerOrderRepository.save(order);
    }
}
