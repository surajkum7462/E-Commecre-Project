package com.ecom.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repo.CartRepo;
import com.ecom.repo.ProductRepo;
import com.ecom.repo.UserRepo;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private CartRepo cartRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	
	
	@Override
	public Cart saveCart(int productId, int userId) {
		UserDtls userDtls = userRepo.findById(userId).get();
		Product product = productRepo.findById(productId).get();
		
		Cart cart = null;
		Cart cartStatus = cartRepo.findByProductIdAndUserId(productId, userId);
		if(ObjectUtils.isEmpty(cartStatus))
		{
			cart=new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		}
		else
		{
			cart=cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}
		Cart saveCart = cartRepo.save(cart);
		return saveCart;
	}
	

	@Override
	public List<Cart> getCartByUser(int userId) {
		List<Cart> carts = cartRepo.findByUserId(userId);
		Double totalOrderPrice = 0.0;
		List<Cart> updateCarts = new ArrayList<>();
		for(Cart c  : carts)
		{
		    Double	totalPrice=c.getProduct().getDiscountPrice() * c.getQuantity();
		    c.setTotalPrice(totalPrice);
		    totalOrderPrice+=totalPrice;
		    c.setTotalOrderPrice(totalOrderPrice);
		    updateCarts.add(c);
		}
		
		return updateCarts;
	}


	@Override
	public int getCountCart(int userId) {
		int countByUserId = cartRepo.countByUserId(userId);
		return countByUserId;
	}


	@Override
	public void updateQuantity(String sy, int cid) {
		Cart cart = cartRepo.findById(cid).get();
		
		int updateQuantity;
		
		if(sy.equals("de"))
		{
			updateQuantity=cart.getQuantity()-1;
			if(updateQuantity <= 0)
			{
				cartRepo.delete(cart);
				
			}
			else
			{
				cart.setQuantity(updateQuantity);
				cartRepo.save(cart);
			}
		}
		else
		{
			updateQuantity=cart.getQuantity()+1;
			cart.setQuantity(updateQuantity);
			cartRepo.save(cart);
		}
		
		
	}

}
