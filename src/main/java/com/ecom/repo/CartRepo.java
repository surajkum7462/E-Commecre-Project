package com.ecom.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Cart;

public interface CartRepo extends JpaRepository<Cart, Integer>{

	public Cart findByProductIdAndUserId(int productId , int userId);

	public int countByUserId(int userId);

	public List<Cart> findByUserId(int userId);
}
