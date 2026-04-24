package com.ws101.EulinMalobago.EcommerceApi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ws101.EulinMalobago.EcommerceApi.dto.OrderRequest;
import com.ws101.EulinMalobago.EcommerceApi.model.CustomerOrder;
import com.ws101.EulinMalobago.EcommerceApi.service.OrderService;

/**
 * @author Eulin Malobago
 * this orderController 
 */

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody OrderRequest request) {
        CustomerOrder createdOrder = orderService.createOrder(request);

        return ResponseEntity
                .created(URI.create("/api/v1/orders/" + createdOrder.getId()))
                .body(createdOrder);
    }
}
