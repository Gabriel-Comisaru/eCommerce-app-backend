package com.qual.store.service;

import com.qual.store.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> saveProduct(Product product);
    Optional<Product> updateProduct(Long id,Product product);
    void deleteProductById(Long id);

}
