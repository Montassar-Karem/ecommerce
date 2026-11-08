package com.kata.ecommerce.catalog.service;

import com.kata.ecommerce.catalog.domain.Category;
import com.kata.ecommerce.catalog.domain.Product;
import com.kata.ecommerce.catalog.dto.ProductRequest;
import com.kata.ecommerce.catalog.dto.ProductResponse;
import com.kata.ecommerce.catalog.mapper.CatalogMapper;
import com.kata.ecommerce.catalog.repository.ProductRepository;
import com.kata.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CatalogMapper mapper;

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.price(), request.stockQuantity());
        return mapper.toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityById(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        return mapper.toResponse(product);
    }

    public void delete(Long id) {
        Product product = findEntityById(id);
        for (Category category : new HashSet<>(product.getCategories())) {
            category.unlinkProduct(product);
        }
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }
}
