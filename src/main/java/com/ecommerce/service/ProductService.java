package com.ecommerce.service;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public ProductDto create(ProductDto dto) {
        Product p = modelMapper.map(dto, Product.class);
        Product saved = productRepository.save(p);
        return modelMapper.map(saved, ProductDto.class);
    }

    public ProductDto update(Long id, ProductDto dto) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setCategory(dto.getCategory());
        existing.setImageUrl(dto.getImageUrl());
        existing.setRating(dto.getRating());
        Product updated = productRepository.save(existing);
        return modelMapper.map(updated, ProductDto.class);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) throw new ResourceNotFoundException("Product not found");
        productRepository.deleteById(id);
    }

    public ProductDto getById(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return modelMapper.map(p, ProductDto.class);
    }

    public Page<ProductDto> list(String category, Double minPrice, Double maxPrice, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Product> pageData;
        if (category != null && !category.isBlank()) {
            pageData = productRepository.findByCategoryIgnoreCase(category, pageable);
        } else if (minPrice != null && maxPrice != null) {
            pageData = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else {
            pageData = productRepository.findAll(pageable);
        }
        return pageData.map(p -> modelMapper.map(p, ProductDto.class));
    }
}
