package org.example.orderservice.dto;



import lombok.Data;
import java.math.BigDecimal;
import java.util.List;


@Data
public class OrderDTO {
    private Long id;
    private String customerName;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }

}