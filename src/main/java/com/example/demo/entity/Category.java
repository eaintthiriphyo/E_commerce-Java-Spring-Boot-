package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;


import org.springframework.validation.annotation.Validated;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
    @NotNull(message = "Name cannot be null")
	private String title;
	private String image;
	
	@OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Product> products=new ArrayList<>();
	
	public Category() {
		super();
	}
	public Category(int id, String title, String image, List<Product> products) {
		super();
		this.id = id;
		this.title = title;
		this.image = image;
		this.products = products;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public void addProduct(Product p) {
		products.add(p);
		p.setCategory(this);
		
	}
	public void deleteProduct(Product p) {
		products.remove(p);
		p.setCategory(null);
	}
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
}
