package com.example.demo.reposistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order_Product;

@Repository
public interface OrderProductReposistory extends JpaRepository<Order_Product, Integer>{

	public List<Order_Product> findByOrderId(int o_id);
}
