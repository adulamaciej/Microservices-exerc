package org.example.productservice.mapper;

import org.example.commondto.dto.ProductDTO;
import org.example.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDto(Product product);
    Product toEntity(ProductDTO productDTO);
    List<ProductDTO> toDtoList(List<Product> products);
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);

}