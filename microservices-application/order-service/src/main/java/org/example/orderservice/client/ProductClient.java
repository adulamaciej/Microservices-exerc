package org.example.orderservice.client;


import org.example.commondto.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(
        name = "product-service",
        path = "/api/products",
        fallback = ProductClientFallback.class)
@Primary
public interface ProductClient {


    @GetMapping
     ResponseEntity<List<ProductDTO>> getAllProducts();

    @GetMapping("/search")
    ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name);

    @GetMapping("/available")
    ResponseEntity<List<ProductDTO>> getAvailableProducts(
            @RequestParam(defaultValue = "0") Integer minQuantity);

    @GetMapping("/{id}")
     ResponseEntity<ProductDTO> getProductById(@PathVariable Long id);

    @PostMapping
     ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO);

    @PutMapping("/{id}")
     ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO);

    @PutMapping("/{id}/reduce-stock")
    ResponseEntity<ProductDTO> reduceStock(@PathVariable Long id, @RequestParam Integer quantity);

    @DeleteMapping("/{id}")
     ResponseEntity<Void> deleteProduct(@PathVariable Long id);

}

