package com.example.demo.reposistory;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.Category;

@Repository
public interface CategoryReposistory extends  JpaRepository<Category, Integer>{

	public boolean existsByTitle(String title);
	
}
