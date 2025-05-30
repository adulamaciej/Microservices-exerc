package org.example.productservice.service;


import lombok.RequiredArgsConstructor;
import org.example.commondto.dto.ProductDTO;
import org.example.productservice.exception.InsufficientStockException;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDTO reduceStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + id);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toDtoList(products);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Product with name '" + productDTO.getName() + "' already exists");
        }
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (!product.getName().equals(productDTO.getName()) &&
                productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Product with name '" + productDTO.getName() + "' already exists");
        }

        productMapper.updateProductFromDto(productDTO, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (product.getStockQuantity() > 0) {
            throw new IllegalStateException("Cannot delete product - has remaining stock: " + product.getStockQuantity());
        }

        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        return productMapper.toDtoList(products);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findAvailableProducts() {
        List<Product> products = productRepository.findByStockQuantityGreaterThan(0);
        return productMapper.toDtoList(products);
    }
}