package org.example.productservice.repository;

import org.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContaining(String name);
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    boolean existsByName(String name);
    Optional<Product> findByName(String name);

}
