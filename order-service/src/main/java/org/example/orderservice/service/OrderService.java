package org.example.orderservice.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.commondto.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.client.ProductClient;
import org.example.orderservice.dto.OrderDTO;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.model.Order;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;



@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    public static final String PRODUCT_SERVICE = "productService";



    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "createOrderFallback")
    public OrderDTO createOrder(OrderDTO orderDto) {
        validateProductsAvailability(orderDto);

        Order order = orderMapper.toEntity(orderDto);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotal(order));

        boolean allStocksUpdated = true;
        for (OrderDTO.OrderItemDTO item : orderDto.getItems()) {
            ResponseEntity<ProductDTO> response = productClient.reduceStock(item.getProductId(), item.getQuantity());
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                allStocksUpdated = false;
                break;
            }
        }
        if (!allStocksUpdated) {
            throw new RuntimeException("Failed to update stock for one or more products");
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    public OrderDTO createOrderFallback(OrderDTO orderDto, Exception ex) {
        throw new RuntimeException("Unable to process order due to service unavailability: " + ex.getMessage());
    }

    private void validateProductsAvailability(OrderDTO orderDto) {
        orderDto.getItems().forEach(item -> {
            ProductDTO product = Optional.ofNullable(productClient.getProductById(item.getProductId()).getBody())
                    .filter(p -> !"Unavailable product".equals(p.getName()))
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductId());
            }

            item.setUnitPrice(product.getPrice());
        });
    }


    private BigDecimal calculateTotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDto(order);
    }
}