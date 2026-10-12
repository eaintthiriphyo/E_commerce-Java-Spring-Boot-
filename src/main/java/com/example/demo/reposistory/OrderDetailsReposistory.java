package com.example.demo.reposistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrderDetails;
import com.example.demo.entity.Order_Product;

@Repository
public interface OrderDetailsReposistory extends JpaRepository<OrderDetails, Integer>{

	public List<OrderDetails> findByUserEmail(String email);
	public List<OrderDetails> findByUserId(Integer u_id);

}
