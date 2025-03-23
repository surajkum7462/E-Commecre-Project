package com.ecom.repo;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

public interface ProductRepo extends JpaRepository<Product, Integer>{

	public List<Product> findByIsActiveTrue();

	public List<Product> findByCategory(String category);
	
	public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2);

	public Page<Product> findByIsActiveTrue(PageRequest pagebale);

	public Page<Product> findByCategory(PageRequest pagebale,String category);

	public Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			PageRequest pageble);

	public Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			PageRequest pageble);

    

	

}
