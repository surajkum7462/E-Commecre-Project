package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repo.CartRepo;
import com.ecom.repo.ProductOrderRepo;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;


@Service
public class OrderServiceImpl implements OrderService{

	@Autowired
	private ProductOrderRepo orderRepo;
	
	
	@Autowired
	private CartRepo cartRepo;
	
	@Autowired
	private CommonUtil commonUtil;
	
	
	
	
	@Override
	public void saveOrder(int userId,OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException {
		List<Cart> carts = cartRepo.findByUserId(userId);
		for(Cart cart:carts)
		{
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			 
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			
			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());
			
			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());
			
			order.setOrderAddress(address);
			
			ProductOrder saveOrder = orderRepo.save(order);
			commonUtil.sendMailProductOrder(saveOrder, "Successfull");
		}
	
	}




	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> orders=orderRepo.findByUserId(userId);
		return orders;
	}




	@Override
	public ProductOrder updateOrderStatus(int id, String st) {
		Optional<ProductOrder> findByid = orderRepo.findById(id);
		if(findByid.isPresent())
		{
			ProductOrder productOrder = findByid.get();
			productOrder.setStatus(st);
			ProductOrder updateOrder = orderRepo.save(productOrder);
			return updateOrder;
		}
		return null;
	}




	@Override
	public List<ProductOrder> getAllOrders() {
		return 	orderRepo.findAll();
		
	}




	@Override
	public ProductOrder getOrdersByOrderId(String id) {
		return orderRepo.findByOrderId(id);
		 
	}




	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		PageRequest pageble = PageRequest.of(pageNo, pageSize);
		
		return orderRepo.findAll(pageble);
	}

}
