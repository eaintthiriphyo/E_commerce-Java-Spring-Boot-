package com.example.demo.reposistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserReposistory extends JpaRepository<User, Integer>{

	public User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(String email);
	
	public User getUserByRole(String role);

	public boolean existsByEmail(String email);
}
