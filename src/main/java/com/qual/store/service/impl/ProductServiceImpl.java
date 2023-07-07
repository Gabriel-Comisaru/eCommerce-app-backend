package com.qual.store.service.impl;

import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Category;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.ProductService;
import com.qual.store.utils.validators.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Validator<Product> validator;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    @Log
    public Product saveProductCategory(Product product, Long categoryId) {
        validator.validate(product);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No category found with id %s", categoryId)));

        product.setCategory(category);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        product.setUser(appUser);

        return productRepository.save(product);
    }

    @Override
    @Log
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }

    @Override
    @Log
    public void saveProduct(Product product) {
        validator.validate(product);

        productRepository.save(product);
    }

    @Transactional
    @Override
    @Log
    public Optional<Product> updateProduct(Long id, Product product) {
        validator.validate(product);

        Optional<Product> optionalProduct = productRepository.findAllWithCategory()
                .stream().filter(pro -> pro.getId().equals(id)).findFirst();

//        Optional<Product> optionalProduct = productRepository.findById(id);

        optionalProduct
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product found with id %s", id)));

        optionalProduct
                .ifPresent(updateProduct -> {
                    updateProduct.setName(product.getName());
                    updateProduct.setPrice(product.getPrice());
                    updateProduct.setDescription(product.getDescription());

                });

        return productRepository.findById(id);
    }

    @Override
    @Log
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product with is found:%s", id)));
    }

    @Override
    @Transactional
    @Log
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product with is found:%s", id)));

        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow();

        category.getProducts().remove(product);
        categoryRepository.save(category);

        productRepository.deleteById(id);
    }

    @Override
    public PaginatedProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));

        Page<Product> page = productRepository.findAll(pageable);

        return PaginatedProductResponse.builder()
                .products(page.getContent().stream()
                        .map(product -> productConverter.convertModelToDto(product))
                        .collect(Collectors.toList()))
                .numberOfItems(page.getTotalElements())
                .numberOfPages(page.getTotalPages())
                .build();
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findAllWithCategory().stream().filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product with id %s is found", productId)));

        return productConverter.convertModelToDto(product);
    }
}
