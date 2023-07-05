package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.ProductConverter;
import com.qual.store.converter.lazyConverter.ProductLazyConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.lazyDto.ProductDtoWithCategory;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Category;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.service.CategoryService;
import com.qual.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductLazyConverter productLazyConverter;

    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping()
    @Log
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(product -> productConverter.convertModelToDto(product))
                .collect(Collectors.toList());
    }

    @GetMapping("/{productId}")
    @Log
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/lazy")
    @Log
    public List<ProductDtoWithCategory> getAllProductsWithCategory() {
        return productService.getAllProducts().stream()
                .map(product -> productLazyConverter.convertModelToDto(product))
                .collect(Collectors.toList());
    }

    @PostMapping("/category/{categoryId}")
    @Log
    public ResponseEntity<?> addProductCategory(@RequestParam String name,
                                                @RequestParam String description,
                                                @RequestParam double price,
                                                @RequestParam MultipartFile file,
                                                @PathVariable Long categoryId) {
        try {
            Product savedProduct = productService.saveProductCategory(Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .reviews(List.of())
                    .build(), file, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productConverter.convertModelToDto(savedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{productId}")
    @Log
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            Product productUpdated = productService.updateProduct(productId, product)
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

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{productId}")
    @Log
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok().body(String.format("Product with id %s deleted", productId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/display")
    @Log
    public ResponseEntity<PaginatedProductResponse> getProducts(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(productService.getProducts(pageNumber, pageSize, sortBy));
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/populate")
    @Log
    public ResponseEntity<?> populateDatabase() {
        try {
            Faker faker = new Faker();

            // Retrieve all categories from the database
            List<Category> categories = categoryService.getAllCategories();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            AppUser appUser = appUserRepository.findUserByUsername(currentUsername);

            for (int i = 0; i < 20; i++) {
                String name = faker.commerce().productName();
                String description = faker.lorem().sentence();
                double price = faker.number().randomDouble(2, 1, 1000);

                Product product = new Product();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);

                // Set a random category for the product
                int randomIndex = faker.random().nextInt(categories.size());
                Category randomCategory = categories.get(randomIndex);
                product.setCategory(randomCategory);

                //set also the user that created the product
                product.setUser(appUser);

                productService.saveProduct(product);
                
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Database populated with fake data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
