package com.example.demo.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.security.AuthSuccessHandlerImpl;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Product;

import com.example.demo.entity.OrderDetails;
import com.example.demo.entity.Order_Product;
import com.example.demo.entity.Payment;
import com.example.demo.entity.User;
import com.example.demo.reposistory.CartReposistory;
import com.example.demo.reposistory.OrderDetailsReposistory;
import com.example.demo.reposistory.OrderProductReposistory;
import com.example.demo.reposistory.PaymentReposistory;

@Service
public class OrderService {
 

	@Autowired
	private OrderDetailsReposistory orderDetailsRepo;
	@Autowired
	private CartReposistory cartRepo;
	@Autowired
	private OrderProductReposistory orderproductRepo;
	
	
	 @Transactional
	public void saveOrder(User user, Payment paymentType) {
		List<Cart> carts=cartRepo.findByUser_Id(user.getId());
			
		if(carts.isEmpty()) {
            throw new RuntimeException("Cart is empty");
		}
		OrderDetails order=new OrderDetails();
		order.setUser(user);
		order.setOrder_status("Order Processing");
		order.setPayment(paymentType);;
	
		List<Order_Product> orderItems=new ArrayList<>();
		double totalPrice=0;
		for(Cart c:carts) {
			Order_Product op=new Order_Product();
			op.setOrder(order);
			op.setProduct(c.getProduct());
			op.setQuantity(c.getQuantity());
			totalPrice+=c.getProduct().getDiscountPrice()*c.getQuantity();
			orderItems.add(op);
	        Product product = c.getProduct();
	        product.setStock(product.getStock()- c.getQuantity());

		}
		order.setOrder_products(orderItems);
		order.setTotal_price(totalPrice);
		 order.setPayment(paymentType);
	
		orderDetailsRepo.save(order);
		cartRepo.deleteByUser(user);
	}
	 

	 public List<OrderDetails> getOrderListByUserId(int u_id) {

		 List<OrderDetails> orderList=orderDetailsRepo.findByUserId(u_id);
		 
		 for(OrderDetails order:orderList) {
			 List<Order_Product> orderProduct=orderproductRepo.findByOrderId(order.getId());
		
	 }
		 return orderList;
		 
	 }
	 
	 public List<OrderDetails> allOrderLisr() {

		 List<OrderDetails> orderLists=orderDetailsRepo.findAll();
		 
		 for(OrderDetails order:orderLists) {
			 List<Order_Product> orderProduct=orderproductRepo.findByOrderId(order.getId());
		
	 }
		 return orderLists;
		 
	 }


	 public OrderDetails orderStatusUpdate(Integer orderId,String status) {

		OrderDetails order=orderDetailsRepo.findById(orderId).orElseThrow(()->new RuntimeException("Order Not Found"));
		order.setOrder_status(status);
		return orderDetailsRepo.save(order);
		
	 }
	 
	 public OrderDetails getOrderById(Integer orderId) {

			OrderDetails order=orderDetailsRepo.findById(orderId).orElseThrow(()->new RuntimeException("Order Not Found"));
			return order;
			
		 }

}
