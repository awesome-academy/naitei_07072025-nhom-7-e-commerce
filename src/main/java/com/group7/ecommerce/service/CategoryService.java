package com.group7.ecommerce.service;
import java.util.List;

import com.group7.ecommerce.dto.request.CategoryDto;
import com.group7.ecommerce.dto.response.CategoryResp;
import com.group7.ecommerce.entity.Category;

public interface CategoryService {
	List<Category> findAll();
	List<CategoryResp> findAllAsTree();
	Category findById(Long id);
	Category save(CategoryDto categoryDto);
	Category update(Long id, CategoryDto categoryDto);
	void deleteById(Long id);
}
