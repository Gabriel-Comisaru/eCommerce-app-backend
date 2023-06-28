package com.qual.store.service;

import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Product saveProductCategory(Product product, Long categoryId);

    void saveProduct(Product product);

    Optional<Product> updateProduct(Long id, Product product);

    Product findProductById(Long id);

    void deleteProductById(Long id);

    PaginatedProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy);
}
