package com.ws101.EulinMalobago.EcommerceApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ws101.EulinMalobago.EcommerceApi.model.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
}
