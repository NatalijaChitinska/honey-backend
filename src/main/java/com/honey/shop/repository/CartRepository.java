package com.honey.shop.repository;

import com.honey.shop.domain.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    @Query("SELECT c FROM Cart c WHERE c.token = :token")
    Optional<Cart> findByToken(@Param("token") String token);
}
