package com.example.demo.reposistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface CartReposistory extends JpaRepository<Cart, Integer> {

    public Cart findByProduct_IdAndUser_Id(Integer productId, Integer userId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user.id = :userId")
    public int countByUser_Id(Integer userId);

    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM Cart c WHERE c.user.id = :userId")
    int getTotalQuantityByUserId(@Param("userId") Integer userId);
    
    public List<Cart> findByUser_Id(Integer userId);
    
    public void deleteByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    }
