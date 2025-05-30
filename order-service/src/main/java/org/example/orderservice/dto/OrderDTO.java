package org.example.orderservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class OrderDTO {

    private Long id;

    @NotBlank(message = "Customer name cannot be null")
    private String customerName;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items;

    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private LocalDateTime orderDate;



    @Data
    public static class OrderItemDTO {

        private Long productId;

        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        @Positive(message = "Unit price must be positive")
        private BigDecimal unitPrice;
    }

}