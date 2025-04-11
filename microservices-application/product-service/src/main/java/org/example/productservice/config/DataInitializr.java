package org.example.productservice.config;

import lombok.RequiredArgsConstructor;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DataInitializr {

    private final ProductRepository productRepository;


    @Bean
    public CommandLineRunner initData() {
        return args -> {
                Product product1 = Product.builder()
                        .name("Laptop Dell XPS 13")
                        .description("Ultrabook, display 13.3 cal, Intel Core i7, 16GB RAM, 512GB SSD")
                        .price(new BigDecimal("4999.99"))
                        .stockQuantity(10)
                        .build();

                Product product2 = Product.builder()
                        .name("Samsung Galaxy S21")
                        .description("Smartphone, display 6.2 cal, 8GB RAM, 128GB memory")
                        .price(new BigDecimal("3499.99"))
                        .stockQuantity(15)
                        .build();

                Product product3 = Product.builder()
                        .name("Tablet Apple iPad Air")
                        .description("Table, display 10.9 cal, 64GB memory")
                        .price(new BigDecimal("2499.99"))
                        .stockQuantity(8)
                        .build();

                productRepository.saveAll(Arrays.asList(product1,product2,product3));
        };
    }

}


