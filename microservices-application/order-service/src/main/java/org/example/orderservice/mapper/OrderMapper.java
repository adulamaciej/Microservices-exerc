package org.example.orderservice.mapper;


import org.example.orderservice.dto.OrderDTO;
import org.example.orderservice.model.Order;
import org.example.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Order toEntity(OrderDTO orderDto);

    OrderDTO toDto(Order order);

    OrderItem toOrderItem(OrderDTO.OrderItemDTO orderItemDTO);

    OrderDTO.OrderItemDTO toOrderItemDto(OrderItem orderItem);
}