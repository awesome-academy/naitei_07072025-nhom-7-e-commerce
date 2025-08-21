package com.group7.ecommerce.mapper;

import com.group7.ecommerce.dto.request.ProductDto;
import com.group7.ecommerce.dto.request.ProductUpdateDto;
import com.group7.ecommerce.dto.response.ProductResponse;
import com.group7.ecommerce.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Product toEntity(ProductDto dto);

    void updateEntityFromDto(@MappingTarget Product entity, ProductUpdateDto dto);

    @Mapping(target = "isFeatured", source = "featured")
    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "imageUrls", expression = "java(product.getImages().stream().map(img -> img.getImageUrl()).toList())")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);
}
