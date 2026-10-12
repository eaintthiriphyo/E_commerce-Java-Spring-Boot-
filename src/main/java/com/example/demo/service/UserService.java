package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.reposistory.ProductReposistory;
import com.example.demo.reposistory.UserReposistory;

import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	private UserReposistory userRepo;
	@Autowired
	private ProductReposistory productRepo;
	
	
	public User saveRegister(@Valid UserDTO userdto) throws Throwable {
		if (userRepo.existsByEmail(userdto.getEmail())) {
	        throw new Exception("Email already registered");
	    }
		System.out.println("Name is "+userdto.getName());
		  User user = new User();
		    user.setName(userdto.getName());
		    user.setEmail(userdto.getEmail());
		    user.setPhone(userdto.getPhone());
		    user.setAddress(userdto.getAddress());
		    user.setState(userdto.getState());
		    user.setCity(userdto.getCity());
		    user.setPincode(userdto.getPincode());




		    user.setImage(userdto.getImage() != null ? userdto.getImage() : "default.jpg");

		    user.setPassword(userdto.getPassword()); 
		    user.setRole("ROLE_USER");

		    return userRepo.save(user);
		    }
	
	public User GetByUserId(int id) {
		return userRepo.findById(id).orElseThrow(()->new RuntimeException());
	}
	
	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	public Product addToCard(int id) {
		Product product=productRepo.findById(id).orElseThrow(()->new RuntimeException());
		return product;
	}
	
	public List<User> getUserbyRole() {
	    List<User> allUsers = userRepo.findAll();
	    List<User> roleUser = new ArrayList<>();

	    for (User u : allUsers) {
	        if ("ROLE_USER".equals(u.getRole())) {
	            roleUser.add(u);
	        }
	    }

	    return roleUser;
	}
	
	public List<User> getAdminbyRole() {
	    List<User> allUsers = userRepo.findAll();
	    List<User> roleUser = new ArrayList<>();

	    for (User u : allUsers) {
	        if ("ROLE_ADMIN".equals(u.getRole())) {
	            roleUser.add(u);
	        }
	    }

	    return roleUser;
	}

	
	public User saveAdmin(User user) {
		user.setRole("ROLE_ADMIN");
	   return userRepo.save(user);
	}
	public boolean deleteAdmin(int id) {
		User user=userRepo.findById(id).orElseThrow(()->new RuntimeException("No admin Found"));
		 userRepo.delete(user);
		    return true;
		
	}

	public void deleteUser(int id) {

		User user=userRepo.findById(id).orElseThrow(()->new RuntimeException("No admin Found"));
		 user.setActive(false);
		 userRepo.save(user);
	}

	public User updateUser(User oldUser) {
		return userRepo.save(oldUser);
	}

	public void deleteUserAcc(int id) {

		User user=userRepo.findById(id).orElseThrow(()->new RuntimeException("No User Found"));
		userRepo.delete(user);
	}

	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
	    return userRepo.existsByEmail(email);
	}
}


