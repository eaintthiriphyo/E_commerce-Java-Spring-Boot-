package com.example.demo.reposistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

@Repository
public interface ProductReposistory extends JpaRepository<Product, Integer>{

	public List<Product> findByCategory_Title(String categoryTitle);
	

	public List<Product> findByNameContainingIgnoreCase(String keyword);
}
