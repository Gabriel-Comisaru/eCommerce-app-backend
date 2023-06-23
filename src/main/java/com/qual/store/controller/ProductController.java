package com.qual.store.controller;

import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.model.Product;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qual.store.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;
import com.github.javafaker.Faker;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductConverter productConverter;


    @GetMapping()
    public List<ProductDto> getAllCProducts() {
        return productService.getAllProducts().stream()
                .map(product -> productConverter.convertModelToDto(product))

                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
           Product savedProduct = productService.saveProduct(product)
                    .orElseThrow(() -> new IllegalArgumentException("product not saved"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productConverter.convertModelToDto(savedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            Product productUpdated = productService.updateProduct(productId,product)
                    .orElseThrow(() -> new IllegalArgumentException("product not saved"));

            ProductDto responseProductDto = productConverter.convertModelToDto(productUpdated);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseProductDto);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok().body(String.format("Product with id %s deleted", productId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @PostMapping("/populate")
    public ResponseEntity<?> populateDatabase() {
        try {
            Faker faker = new Faker();
            for (int i = 0; i < 100; i++) {
                String name = faker.commerce().productName();
                String description = faker.lorem().sentence();
                double price = faker.number().randomDouble(2, 1, 1000);

                Product product = new Product();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);

                productService.saveProduct(product);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Database populated with fake data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
