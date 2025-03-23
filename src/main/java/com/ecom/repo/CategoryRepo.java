package com.ecom.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer>{
     public boolean existsByName(String name);

	public List<Category> findByIsActiveTrue();
}
