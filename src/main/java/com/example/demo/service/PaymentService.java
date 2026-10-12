package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Payment;
import com.example.demo.reposistory.PaymentReposistory;

@Service
public class PaymentService {

	@Autowired
	private PaymentReposistory paymentRepo;
	
	
	
	public List<Payment> getPaymentList(){
		return paymentRepo.findAll();
	}
	public String getPaymentById(int id) {
		Payment pay=paymentRepo.findById(id).orElseThrow(()->new RuntimeException("No Payment Type Found"));
		String payType=pay.getPayment_type();
		return payType;
	}
}
