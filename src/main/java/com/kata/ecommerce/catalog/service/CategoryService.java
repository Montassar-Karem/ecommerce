package com.kata.ecommerce.catalog.service;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.CategoryRequest;
import com.kata.ecommerce.catalog.dto.CategoryResponse;
import com.kata.ecommerce.catalog.dto.CategoryUpdateRequest;
import com.kata.ecommerce.catalog.mapper.CatalogMapper;
import com.kata.ecommerce.catalog.repository.CategoryRepository;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final CatalogMapper mapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category(request.name(), request.description());
        if (request.parentId() != null) {
            findEntityById(request.parentId()).addSubCategory(category);
        }
        return mapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = findEntityById(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return mapper.toResponse(category);
    }

    public void delete(Long id) {
        Category category = findEntityById(id);
        if (category.getParent() != null) {
            category.getParent().removeSubCategory(category);
        }
        categoryRepository.delete(category);
    }

    public CategoryResponse linkProduct(Long categoryId, Long productId) {
        Category category = findEntityById(categoryId);
        Product product = productService.findEntityById(productId);
        category.linkProduct(product); // Idempotent grâce au Set
        return mapper.toResponse(category);
    }

    public CategoryResponse unlinkProduct(Long categoryId, Long productId) {
        Category category = findEntityById(categoryId);
        Product product = productService.findEntityById(productId);
        category.unlinkProduct(product);
        return mapper.toResponse(category);
    }

    // Sert aussi à déplacer : addSubCategory retire de l'ancien parent et bloque les cycles
    public CategoryResponse attachSubCategory(Long parentId, Long childId) {
        Category parent = findEntityById(parentId);
        Category child = findEntityById(childId);
        parent.addSubCategory(child);
        return mapper.toResponse(parent);
    }

    public CategoryResponse detachSubCategory(Long parentId, Long childId) {
        Category parent = findEntityById(parentId);
        Category child = findEntityById(childId);
        parent.removeSubCategory(child); // 409 si child n'est pas un enfant de parent
        return mapper.toResponse(parent);
    }

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }
}
