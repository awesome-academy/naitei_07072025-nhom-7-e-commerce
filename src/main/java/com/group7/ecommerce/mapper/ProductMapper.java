package com.group7.ecommerce.mapper;

import com.group7.ecommerce.dto.request.ProductDto;
import com.group7.ecommerce.entity.Product;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Product toEntity(ProductDto dto);
}
