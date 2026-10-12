package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.reposistory.CategoryReposistory;

@Service
public class CategoryService {

	@Autowired
	private CategoryReposistory categoryRepo;
	public List<Category> allCategoryList(){
		return categoryRepo.findAll();
	}
	
	public Category getCategoryById(int id) {
		return categoryRepo.findById(id).orElseThrow(()->new RuntimeException("Category Not Found"));
	}
	
	public Category addCategory(Category category) {
		
		return categoryRepo.save(category);
	}
	
	public boolean existsByTitle(String title) {
		return categoryRepo.existsByTitle(title);
				}
	
	public boolean deleteCategoryById(int id) {
		Optional <Category> category=categoryRepo.findById(id);
		if(category.isEmpty()) {
			
			return false;
		}else {
			categoryRepo.delete(category.get());
			return true;
		}
		
		
	}
}


