package com.example.demo.service;
import com.example.demo.service.ProductService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.reposistory.ProductReposistory;

@Service
public class ProductService {


	@Autowired
	public ProductReposistory productRepo;

    
	public Product saveProduct(Product product) {
		return productRepo.save(product);
	}
	public List<Product> allProduct(){
		return productRepo.findAll();
		
	}
	public void deleteProduct(int id) {
		Product p=productRepo.findById(id).orElseThrow(()->new RuntimeException("No Product Found"));
	   productRepo.delete(p);
	}
	
	public Product getProductById(int id) {
		return productRepo.findById(id).orElseThrow(()->new RuntimeException("No Product Found"));
	}
	public List<Product> productByCategory(String categoryTitle){
		return productRepo.findByCategory_Title(categoryTitle);
		
		
	}
	
	public List<Product> searchProduct(String keyword){
		return productRepo.findByNameContainingIgnoreCase(keyword);
		
	}

	
	
}
