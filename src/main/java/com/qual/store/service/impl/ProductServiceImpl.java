package com.qual.store.service.impl;

import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import com.qual.store.repository.OrderItemRepository;
import com.qual.store.service.OrderItemService;
import com.qual.store.utils.validators.Validator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;
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
    @Autowired
    private Validator<OrderItem> orderItemValidator;
    @Autowired
    private OrderItemRepository orderItemRepository;

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

    @Transactional
    @Override
    public Optional<Product> updateProduct(Long id, Product product) {
        validator.validate(product);
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product found with id %s", id)));
        optionalProduct
                .ifPresent(updateProduct -> {
                    updateProduct.setName(product.getName());
                    updateProduct.setPrice(product.getPrice());
                    updateProduct.setDescription(product.getDescription());

                });
        return Optional.ofNullable(productRepository.getReferenceById(id));
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(String.format("No product with is found:%s", id)));
        productRepository.deleteById(id);
    }
}
