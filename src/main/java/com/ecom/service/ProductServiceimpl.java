package com.ecom.service;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repo.ProductRepo;
import com.ecom.util.BucketType;
import com.ecom.util.CommonUtil;

@Service
public class ProductServiceimpl implements ProductService {

	@Autowired
	private ProductRepo productRepo;
	private Product orElse;
	private Product save;
	
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private FileService fileService;

	@Override
	public Product saveProduct(Product product) {
		return productRepo.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepo.findAll();
	}

	@Override
	public Boolean deleteProduct(int id) {
		Product product = productRepo.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepo.delete(product);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Product getProductById(int id) {
		return productRepo.findById(id).orElse(null);
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {

		Product dbProduct = getProductById(product.getId());
		//*String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();
		
		String imageUrl = commonUtil.getImageUrl(image, BucketType.PRODUCT.getId());
		
		dbProduct.setTitle(product.getTitle());
		dbProduct.setCategory(product.getCategory());
		dbProduct.setDescription(product.getDescription());
		dbProduct.setPrice(product.getPrice());
		dbProduct.setStock(product.getStock());
		dbProduct.setImage(imageUrl);
		dbProduct.setIsActive(product.getIsActive());

		dbProduct.setDiscount(product.getDiscount());

		// 5=100*(5/100)--> 100-5

		Double discount = product.getPrice() * (product.getDiscount() / 100.0);
		Double discountprice = product.getPrice() - discount;
		dbProduct.setDiscountPrice(discountprice);

		Product updateProduct = productRepo.save(dbProduct);

		if (!ObjectUtils.isEmpty(updateProduct)) {
			if (!image.isEmpty()) {
				try {

					/*
					 * *File saveFile = new ClassPathResource("static/img").getFile(); Path path =
					 * Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" +
					 * File.separator + image.getOriginalFilename()); System.out.println(path);
					 * Files.copy(image.getInputStream(), path,
					 * StandardCopyOption.REPLACE_EXISTING);
					 */
					fileService.uploadFileS3(image, BucketType.PRODUCT.getId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return product;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if(ObjectUtils.isEmpty(category))
		{
			products=productRepo.findByIsActiveTrue();
		}
		else
		{
			products=productRepo.findByCategory(category);
		}
		
		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		return	productRepo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
		
		
		
		
		 
	}
// Pagination
	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize , String category) {
		
		PageRequest pagebale = PageRequest.of(pageNo, pageSize);
		
		Page<Product> pageProduct=null;
		if(ObjectUtils.isEmpty(category))
		{
			pageProduct=productRepo.findByIsActiveTrue(pagebale);
		}
		else
		{
			pageProduct=productRepo.findByCategory(pagebale,category);
		}
	
		return pageProduct;
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
		PageRequest pageble = PageRequest.of(pageNo, pageSize);
		return productRepo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch,pageble);
	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		PageRequest pageble = PageRequest.of(pageNo, pageSize);
		return productRepo.findAll(pageble);
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category,String ch) {
		

		
		Page<Product> pageProduct=null;
		
		PageRequest pageble = PageRequest.of(pageNo, pageSize);
		  pageProduct = productRepo.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch,pageble);
		
		
		
		
		
	
		return pageProduct;
	}

	
	

}
