package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.reposistory.CartReposistory;
import com.example.demo.reposistory.ProductReposistory;
import com.example.demo.reposistory.UserReposistory;

@Service
public class CartService {

	@Autowired
	private CartReposistory cartRepo;
	@Autowired
	private UserReposistory userRepo;
	
	@Autowired
	private ProductReposistory productRepo;
	public Cart saveCart(int productId,int userId) {
		User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("No User Found"));
		Product p=productRepo.findById(productId).orElseThrow(()->new RuntimeException("No User Found"));
		Cart cartStatus=cartRepo.findByProduct_IdAndUser_Id(productId, userId);
		
		Cart cart=null;
		if(ObjectUtils.isEmpty(cartStatus)) {
			cart=new Cart();
			cart.setProduct(p);
			cart.setUser(user);
			cart.setQuantity(1);
			cart.setTotalPrice(1*p.getDiscountPrice());

		}else {
			cart=cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
		}
		return cartRepo.save(cart);
	}
	
	
	public int getCartCount(int id) {
		int countByuserId=cartRepo.countByUser_Id(id);
		return countByuserId;
	}
	public List<Cart> getCartByUser(int id){
		List<Cart> cartList=cartRepo.findByUser_Id(id);
		 if(cartList.isEmpty()) {
		        return new ArrayList<>();
		 }
		Double totalOrderPrice=0.0;
		List<Cart> updateCart=new ArrayList<>();
		for(Cart c:cartList) {
			Double totalPrice=c.getProduct().getDiscountPrice()*c.getQuantity();
			c.setTotalPrice(totalPrice);
			totalOrderPrice=totalPrice+totalOrderPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updateCart.add(c);
		}
		return updateCart;
	}


	public void updateQuantity(String value, int c_id) {

		Cart cart=cartRepo.findById(c_id).get();
		
		int currentQty=cart.getQuantity();

		int stock=cart.getProduct().getStock();
		if(value.equals("plus")) {
			if(currentQty<stock) {
				cart.setQuantity(currentQty+1);
				
			cart.getProduct().setStock(cart.getProduct().getStock()-1);
			}
		}else if(value.equals("minus")) {
			if(currentQty>1) {
				cart.setQuantity(currentQty-1);
				cart.getProduct().setStock(cart.getProduct().getStock()+1);

			}
		}
		cartRepo.save(cart);
	}


	public void deleteCart(int c_id) {

		Cart cart=cartRepo.findById(c_id).get();
		cartRepo.delete(cart);
		
	}
}

