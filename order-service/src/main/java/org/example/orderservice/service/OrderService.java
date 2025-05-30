package org.example.orderservice.service;


import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.commondto.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.client.ProductClient;
import org.example.orderservice.dto.OrderDTO;
import org.example.orderservice.exception.InsufficientStockException;
import org.example.orderservice.exception.ProductNotFoundException;
import org.example.orderservice.exception.ProductServiceUnavailableException;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.model.Order;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;



@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;



        @Retry(name = "productService")
        public OrderDTO createOrder(OrderDTO orderDto) {
            validateProductsAvailability(orderDto);
            processStockReduction(orderDto);

            Order order = orderMapper.toEntity(orderDto);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalAmount(calculateTotal(order));

            if (order.getItems() != null) {
                order.getItems().forEach(item -> item.setOrder(order));
            }

            Order savedOrder = orderRepository.save(order);
            return orderMapper.toDto(savedOrder);
        }

        @Transactional(readOnly = true)
        public List<OrderDTO> getAllOrders() {
            return orderRepository.findAll().stream()
                    .map(orderMapper::toDto)
                    .toList();
        }

        @Transactional(readOnly = true)
        public OrderDTO getOrderById(Long id) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
            return orderMapper.toDto(order);
        }

        private void validateProductsAvailability(OrderDTO orderDto) {
            orderDto.getItems().forEach(item -> {
                try {
                    ResponseEntity<ProductDTO> response = productClient.getProductById(item.getProductId());
                    ProductDTO product = response.getBody();


                    if (product == null) {
                        throw new ProductNotFoundException("Product not found: " + item.getProductId());
                    }

                    if (product.getStockQuantity() < item.getQuantity()) {
                        throw new InsufficientStockException("Insufficient stock for product: " + item.getProductId() +
                                ". Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity());
                    }

                    item.setUnitPrice(product.getPrice());
                } catch (FeignException.NotFound e) {
                    throw new ProductNotFoundException("Product not found: " + item.getProductId());
                } catch (FeignException e) {
                    throw new ProductServiceUnavailableException("Error while contacting product service: " + e.getMessage());
                }
            });
        }

        private void processStockReduction(OrderDTO orderDto) {
            orderDto.getItems().forEach(item ->
                    productClient.reduceStock(item.getProductId(), item.getQuantity()));
        }

        private BigDecimal calculateTotal(Order order) {
            return order.getItems().stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }