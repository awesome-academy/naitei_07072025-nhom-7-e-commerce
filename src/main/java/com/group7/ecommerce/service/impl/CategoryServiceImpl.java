package com.group7.ecommerce.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.group7.ecommerce.dto.request.CategoryDto;
import com.group7.ecommerce.dto.response.CategoryResp;
import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.exception.ResourceNotFoundException;
import com.group7.ecommerce.repository.CategoryRepository;
import com.group7.ecommerce.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public List<CategoryResp> findAllAsTree() {
		List<Category> allCategories = categoryRepository.findAll();
		Map<Long, List<Category>> childrenByParentId = allCategories.stream()
				.filter(cat -> cat.getParent() != null)
				.collect(Collectors.groupingBy(cat -> cat.getParent().getId()));

		return allCategories.stream()
				.filter(cat -> cat.getParent() == null)
				.map(rootCategory -> buildCategoryTree(rootCategory, childrenByParentId))
				.collect(Collectors.toList());
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Category findById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}

	@Override
	public Category save(CategoryDto categoryDto) {
		Category category = new Category();
		category.setName(categoryDto.getName());
		category.setDescription(categoryDto.getDescription());

		if (categoryDto.getParentId() != null) {
			Category parent = findById(categoryDto.getParentId());
			category.setParent(parent);
		}

		return categoryRepository.save(category);
	}


	@Override public Category update(Long id, CategoryDto categoryDto) { return null; }
	@Override public void deleteById(Long id) {}

	private CategoryResp buildCategoryTree(Category category, Map<Long, List<Category>> childrenMap) {
		List<Category> children = childrenMap.getOrDefault(category.getId(), Collections.emptyList());
		List<CategoryResp> childrenDto = children.stream()
				.map(child -> buildCategoryTree(child, childrenMap))
				.collect(Collectors.toList());

		return new CategoryResp(
				category.getId(),
				category.getName(),
				category.getDescription(),
				childrenDto
				);
	}
}
