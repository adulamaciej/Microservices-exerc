package org.example.orderservice.client;

import org.example.commondto.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ProductClientFallback implements ProductClient {

    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return getAllProductsFallback(null);
    }

    public ResponseEntity<List<ProductDTO>> getAllProductsFallback(Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<List<ProductDTO>> searchProducts(String name) {
        return searchProductsFallback(name, null);
    }

    public ResponseEntity<List<ProductDTO>> searchProductsFallback(String name, Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAvailableProducts(Integer minQuantity) {
        return getAvailableProductsFallback(minQuantity, null);
    }

    public ResponseEntity<List<ProductDTO>> getAvailableProductsFallback(Integer minQuantity, Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Override
    public ResponseEntity<ProductDTO> getProductById(Long id) {
        return getProductByIdFallback(id, null);
    }

    public ResponseEntity<ProductDTO> getProductByIdFallback(Long id, Throwable t) {
        ProductDTO fallbackProduct = ProductDTO.builder()
                .id(id)
                .name("Unavailable product")
                .description("This is fallback data — product not found")
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .build();
        return ResponseEntity.ok(fallbackProduct);
    }

    @Override
    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO) {
        return ResponseEntity.status(503).body(null);
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        return ResponseEntity.status(503).body(null);
    }

    @Override
    public ResponseEntity<ProductDTO> reduceStock(Long id, Integer quantity) {
        return reduceStockFallback(id, quantity, null);
    }

    public ResponseEntity<ProductDTO> reduceStockFallback(Long id, Integer quantity, Throwable t) {
        return ResponseEntity.status(503).body(null);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        return ResponseEntity.status(503).build();
    }
}