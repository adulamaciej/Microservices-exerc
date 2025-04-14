package org.example.orderservice.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.commondto.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@FeignClient(name = "product-service", path = "/api/products", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    ResponseEntity<List<ProductDTO>> getAllProducts();

    @GetMapping("/search")
    @CircuitBreaker(name = "productService", fallbackMethod = "searchProductsFallback")
    ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name);

    @GetMapping("/available")
    @CircuitBreaker(name = "productService", fallbackMethod = "getAvailableProductsFallback")
    ResponseEntity<List<ProductDTO>> getAvailableProducts(
            @RequestParam(defaultValue = "0") Integer minQuantity);

    @GetMapping("/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    ResponseEntity<ProductDTO> getProductById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO);

    @PutMapping("/{id}")
    ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO);

    @PutMapping("/{id}/reduce-stock")
    @CircuitBreaker(name = "productService", fallbackMethod = "reduceStockFallback")
    ResponseEntity<ProductDTO> reduceStock(@PathVariable Long id, @RequestParam Integer quantity);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id);
}


