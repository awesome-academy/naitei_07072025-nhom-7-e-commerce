package com.group7.ecommerce.mapper;

import com.group7.ecommerce.dto.response.CategoryResponse;
import com.group7.ecommerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toCategoryResponse(Category category);
}
