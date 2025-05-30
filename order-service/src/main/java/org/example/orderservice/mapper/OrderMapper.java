package org.example.orderservice.mapper;


import org.example.orderservice.dto.OrderDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    Order toEntity(OrderDTO orderDto);

    OrderDTO toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(OrderDTO.OrderItemDTO orderItemDTO);

    OrderDTO.OrderItemDTO toOrderItemDto(OrderItem orderItem);
}