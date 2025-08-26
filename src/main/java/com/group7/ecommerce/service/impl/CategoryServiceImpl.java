package com.group7.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group7.ecommerce.dto.request.CategoryDto;
import com.group7.ecommerce.dto.response.CategoryResp;
import com.group7.ecommerce.entity.Category;
import com.group7.ecommerce.exception.ResourceNotFoundException;
import com.group7.ecommerce.repository.CategoryRepository;
import com.group7.ecommerce.repository.ProductRepository;
import com.group7.ecommerce.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	@Override
	public List<CategoryResp> findAllAsTree() {
		List<Category> allCategories = categoryRepository.findAllActive();
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


	@Override
	public Category update(Long id, CategoryDto categoryDto) {
		Category categoryToUpdate = findById(id);

		categoryToUpdate.setName(categoryDto.getName());
		categoryToUpdate.setDescription(categoryDto.getDescription());

		if (categoryDto.getParentId() != null) {
			if (categoryDto.getParentId().equals(id)) {
				throw new IllegalArgumentException("Một danh mục không thể là danh mục cha của chính nó.");
			}
			Category parent = findById(categoryDto.getParentId());
			categoryToUpdate.setParent(parent);
		} else {
			categoryToUpdate.setParent(null);
		}

		return categoryRepository.save(categoryToUpdate);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		Category categoryToDelete = findById(id);

		List<Category> allCategories = categoryRepository.findAllActive();
		List<Category> descendants = new ArrayList<>();
		findAllDescendants(categoryToDelete, allCategories, descendants);

		// danh sách các danh mục bị ảnh hưởng
		List<Category> categoriesToDelete = new ArrayList<>();
		categoriesToDelete.add(categoryToDelete);
		categoriesToDelete.addAll(descendants);

		//Lấy ID
		long productCount = productRepository.countActiveProductsInCategoryIds(
				categoriesToDelete.stream()
				.map(Category::getId)
				.collect(Collectors.toSet())
				);
		if (productCount > 0) {
			throw new IllegalStateException("Không thể xóa. Có " + productCount + " sản phẩm đang thuộc về danh mục này hoặc các danh mục con của nó.");
		}
		//XÓA MỀM
		categoriesToDelete.forEach(cat -> cat.setDeleted(true));

		categoryRepository.saveAll(categoriesToDelete);
	}

	private void findAllDescendants(Category parent, List<Category> allCategories, List<Category> descendants) {
		List<Category> children = allCategories.stream()
				.filter(c -> c.getParent() != null && c.getParent().getId() == parent.getId())
				.collect(Collectors.toList());

		for (Category child : children) {
			descendants.add(child);
			findAllDescendants(child, allCategories, descendants);
		}
	}

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
