package com.qual.store.service;

import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Product saveProductCategory(Product product,Long categoryId);
    Product saveProduct(Product product);
    Optional<Product> updateProduct(Long id,Product product);
    Product findProductById(Long id);

    void deleteProductById(Long id);
}
