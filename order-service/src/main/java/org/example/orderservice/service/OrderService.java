package org.example.orderservice.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
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
    @Retry(name = PRODUCT_SERVICE)
    public OrderDTO createOrder(OrderDTO orderDto) {
        validateProductsAvailability(orderDto);

        Order order = orderMapper.toEntity(orderDto);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotal(order));

        processStockReduction(orderDto);

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
            ResponseEntity<ProductDTO> response = productClient.getProductById(item.getProductId());

            if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                throw new ProductServiceUnavailableException("Product service temporarily unavailable");
            }

            ProductDTO product = Optional.ofNullable(response.getBody())
                    .filter(p -> !"Unavailable product".equals(p.getName()))
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + item.getProductId() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity());
            }
            item.setUnitPrice(product.getPrice());
        });
    }




    private void processStockReduction(OrderDTO orderDto) {
        for (OrderDTO.OrderItemDTO item : orderDto.getItems()) {
            ResponseEntity<ProductDTO> response = productClient.reduceStock(item.getProductId(), item.getQuantity());

            if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                throw new ProductServiceUnavailableException("Cannot process order - product service unavailable");
            }

            ProductDTO responseBody = response.getBody();
            if (responseBody != null && responseBody.getStockQuantity() < 0) {
                throw new ProductServiceUnavailableException("Failed to update stock for product: " + item.getProductId());
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ProductServiceUnavailableException("Failed to update stock for product: " + item.getProductId());
            }
        }
    }

    private BigDecimal calculateTotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    public OrderDTO createOrderFallback(OrderDTO orderDto, Exception ex) {
        throw new ProductServiceUnavailableException("Unable to process order due to service unavailability: " + ex.getMessage());
    }


}