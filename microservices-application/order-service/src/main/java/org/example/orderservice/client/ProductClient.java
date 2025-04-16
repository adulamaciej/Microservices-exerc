package org.example.orderservice.client;


import org.example.commondto.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Primary
@FeignClient(name = "product-service", path = "/api/products", fallback = ProductClientFallback.class)
public interface ProductClient {


    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable Long id);


    @PutMapping("/{id}/reduce-stock")
    ResponseEntity<ProductDTO> reduceStock(@PathVariable Long id, @RequestParam Integer quantity);

}


