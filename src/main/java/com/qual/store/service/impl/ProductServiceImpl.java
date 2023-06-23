package com.qual.store.service.impl;

import com.qual.store.model.Product;
import com.qual.store.utils.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qual.store.repository.ProductRepository;
import com.qual.store.service.ProductService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Validator<Product> validator;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> saveProduct(Product product) {
      validator.validate(product);
       Product savedproduct = productRepository.save(product);
      return Optional.of(savedproduct);

    }

    @Override
    public Optional<Product> updateProduct(Long id, Product product) {
        return Optional.empty();
    }

    @Override
    public void deleteProductById(Long id) {

    }


}
