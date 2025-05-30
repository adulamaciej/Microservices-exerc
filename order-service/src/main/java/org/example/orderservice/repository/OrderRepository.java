package org.example.orderservice.repository;

import org.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items")
    List<Order> findAll();

    @EntityGraph(attributePaths = "items")
    Optional<Order> findById(Long id);




}