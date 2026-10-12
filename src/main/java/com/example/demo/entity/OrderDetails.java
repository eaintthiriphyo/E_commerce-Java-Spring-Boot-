package com.example.demo.entity;


import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
public class OrderDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	
	@ManyToOne
	@JoinColumn(name="payment_id")
	private Payment payment;
	
	
	@OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Order_Product> order_products;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	
	private String order_status;
	private Double total_price;
	private  LocalDate order_date;
	
	 @PrePersist
	    protected void onCreate() {
	        this.order_date = LocalDate.now();
	    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	
	public List<Order_Product> getOrder_products() {
		return order_products;
	}
	public void setOrder_products(List<Order_Product> order_products) {
		this.order_products = order_products;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public Double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(Double total_price) {
		this.total_price = total_price;
	}
	public  LocalDate getOrder_date() {
		return order_date;
	}
	public void setOrder_date(LocalDate order_date ) {
		this.order_date = order_date;
	}


	
}
