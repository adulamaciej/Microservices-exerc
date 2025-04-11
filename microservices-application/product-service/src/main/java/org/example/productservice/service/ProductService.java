package org.example.productservice.service;

import lombok.RequiredArgsConstructor;
import org.example.commondto.dto.ProductDTO;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDTO reduceStock(Long id, Integer quantity) {
        log.info("Reducing stock for product id: {} by quantity: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new NoSuchElementException("Product not found with id: " + id);
                });

        if (product.getStockQuantity() < quantity) {
            log.error("Insufficient stock for product id: {}, requested: {}, available: {}",
                    id, quantity, product.getStockQuantity());
            throw new IllegalArgumentException("Insufficient stock for product: " + id);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        Product updatedProduct = productRepository.save(product);
        log.info("Stock reduced successfully for product id: {}, new quantity: {}",
                id, updatedProduct.getStockQuantity());

        return productMapper.toDto(updatedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products in database", products.size());

        if (log.isDebugEnabled()) {
            products.forEach(p -> log.debug("Product: id={}, name={}, price={}, stock={}",
                    p.getId(), p.getName(), p.getPrice(), p.getStockQuantity()));
        }

        List<ProductDTO> dtos = productMapper.toDtoList(products);
        log.info("Mapped {} products to DTOs", dtos.size());

        if (log.isDebugEnabled()) {
            dtos.forEach(dto -> log.debug("ProductDTO: id={}, name={}, price={}, stock={}",
                    dto.getId(), dto.getName(), dto.getPrice(), dto.getStockQuantity()));
        }

        return dtos;
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new NoSuchElementException("Product not found with id: " + id);
                });

        ProductDTO dto = productMapper.toDto(product);
        log.info("Retrieved product: {}", dto.getName());
        return dto;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.getName());

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        log.info("Successfully created product with id: {}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for update with id: {}", id);
                    return new NoSuchElementException("Product not found with id: " + id);
                });

        productMapper.updateProductFromDto(productDTO, product);
        Product updatedProduct = productRepository.save(product);

        log.info("Successfully updated product with id: {}", updatedProduct.getId());
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        if (!productRepository.existsById(id)) {
            log.error("Product not found for deletion with id: {}", id);
            throw new NoSuchElementException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
        log.info("Successfully deleted product with id: {}", id);
    }

    public List<ProductDTO> searchProductsByName(String name) {
        log.info("Searching products by name containing: {}", name);

        List<Product> products = productRepository.findByNameContaining(name);
        log.info("Found {} products matching search criteria", products.size());

        return productMapper.toDtoList(products);
    }

    public List<ProductDTO> findAvailableProducts(Integer minimumQuantity) {
        log.info("Finding available products with stock quantity > {}", minimumQuantity);

        List<Product> products = productRepository.findByStockQuantityGreaterThan(minimumQuantity);
        log.info("Found {} products with available stock", products.size());

        return productMapper.toDtoList(products);
    }
}