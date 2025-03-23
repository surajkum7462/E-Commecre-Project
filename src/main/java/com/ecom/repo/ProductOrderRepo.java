package com.ecom.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.ProductOrder;

public interface ProductOrderRepo extends JpaRepository<ProductOrder, Integer>{

	List<ProductOrder> findByUserId(int userId);

	ProductOrder findByOrderId(String id);

}
