package com.ecom.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;

public interface OrderService {
	
	public void saveOrder(int userId,OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;
	
	public List<ProductOrder> getOrdersByUser(Integer userId);

	public ProductOrder updateOrderStatus(int id , String st);
	
	public List<ProductOrder> getAllOrders();
	
	public ProductOrder getOrdersByOrderId(String id);
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo,Integer pageSize);
}
