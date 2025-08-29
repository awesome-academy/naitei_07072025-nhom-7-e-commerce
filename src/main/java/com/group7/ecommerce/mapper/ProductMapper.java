package com.group7.ecommerce.mapper;

import com.group7.ecommerce.dto.request.ProductDto;
import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.dto.response.ProductResponse;
import com.group7.ecommerce.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Product toEntity(ProductDto dto);

    @Mapping(target = "deleted", expression = "java(dto.isDeleted() != null ? dto.isDeleted() : false)")
    @Mapping(target = "featured", expression = "java(dto.isFeatured() != null ? dto.isFeatured() : false)")
    void updateEntityFromDto(@MappingTarget Product entity, ProductUpdateDto dto);

    @Mapping(target = "isFeatured", source = "featured")
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "imageUrls", expression = "java(product.getImages().stream().map(img -> img.getImageUrl()).toList())")
    ProductResponse toResponse(Product product);
}
