package com.example.ecomweb.repository;

import com.example.ecomweb.entity.CartItem;
import com.example.ecomweb.entity.User;
import com.example.ecomweb.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
    long countByUser(User user);
}