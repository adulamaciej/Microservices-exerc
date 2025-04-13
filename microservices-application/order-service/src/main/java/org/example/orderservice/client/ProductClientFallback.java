package org.example.orderservice.client;

import org.example.commondto.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductClientFallback implements ProductClient {


    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<List<ProductDTO>> searchProducts(String name) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAvailableProducts(Integer minQuantity) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<ProductDTO> getProductById(Long id) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO) {
        throw new RuntimeException("Product service is not available");
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        throw new RuntimeException("Product service is not available");
    }

    @Override
    public ResponseEntity<ProductDTO> reduceStock(Long id, Integer quantity) {
        throw new RuntimeException("Cannot reduce stock: Product service is not available");
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        throw new RuntimeException("Product service is not available");
    }
}
