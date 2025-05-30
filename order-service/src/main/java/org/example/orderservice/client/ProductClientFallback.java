package org.example.orderservice.client;

import org.example.commondto.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class ProductClientFallback implements ProductClient {

    // example of a fallback

    @Override
    public ResponseEntity<ProductDTO> getProductById(Long id) {

        ProductDTO fallbackProduct = ProductDTO.builder()
                .id(id)
                .name("Product temporarily unavailable")
                .description("Please try again later")
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .build();

        return ResponseEntity.status(503).body(fallbackProduct);
    }

    @Override
    public ResponseEntity<ProductDTO> reduceStock(Long id, Integer quantity) {
        ProductDTO fallbackResponse = ProductDTO.builder()
                .id(id)
                .name("Service temporarily unavailable")
                .description("Cannot reduce stock - service down")
                .stockQuantity(-1)
                .build();
        return ResponseEntity.status(503).body(fallbackResponse);
    }
}