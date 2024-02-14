package com.qual.store.controller;

import com.github.javafaker.Faker;
import com.qual.store.converter.ProductConverter;
import com.qual.store.converter.lazyConverter.ProductLazyConverter;
import com.qual.store.dto.MessageResponse;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.lazyDto.ProductDtoWithCategory;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.logger.Log;
import com.qual.store.model.AppUser;
import com.qual.store.model.Category;
import com.qual.store.model.Product;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.service.CategoryService;
import com.qual.store.service.OrderService;
import com.qual.store.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;
    private final ProductConverter productConverter;
    private final CategoryService categoryService;
    private final ProductLazyConverter productLazyConverter;
    private final AppUserRepository appUserRepository;

    @GetMapping()
    @Log
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(productConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/discount")
    @Log
    public ResponseEntity<List<ProductDto>> getAllProductsByDiscount() {
        return ResponseEntity.ok()
                .body(productService.getAllProductsByDiscount().stream()
                        .map(productConverter::convertModelToDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/price")
    @Log
    public ResponseEntity<List<ProductDto>> getAllProductsByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return ResponseEntity.ok()
                .body(productService.getAllProductsByPriceRange(minPrice, maxPrice).stream()
                        .map(productConverter::convertModelToDto)
                        .collect(Collectors.toList()));
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
                .map(productLazyConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

    @PostMapping(
            path = "/category/{categoryId}",
            consumes = {"multipart/form-data"}
    )
    @Log
    public ResponseEntity<?> addProductCategory(@ModelAttribute @Valid ProductRequestDto productRequestDto,
                                                @PathVariable Long categoryId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productConverter.convertModelToDto(
                                productService.saveProductCategory(productRequestDto, categoryId)
                        )
                );
    }

    @PutMapping(
            path = "/{productId}",
            consumes = {"*/*"}
    )
    @Log
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestBody @Valid ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productConverter.convertModelToDto(
                        productService.updateProduct(productId, productRequestDto)
                ));
    }

    @DeleteMapping("/{productId}")
    @Log
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body(String.format("Product with id %s deleted", productId));
    }

    @GetMapping("/display")
    @Log
    public ResponseEntity<PaginatedProductResponse> getProducts(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(productService.getProducts(pageNumber, pageSize, sortBy));
    }

    @GetMapping("/category")
    @Log
    public List<ProductDto> getProductsByCategory(@RequestParam Long categoryId) {
        return productService.findProductsByCategory(categoryId).stream()
                .map(productConverter::convertModelToDto)
                .collect(Collectors.toList());
    }

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

    @GetMapping("/placed")
    @Log
    public List<ProductDto> getProductsByOrderStatusPlaced() {
        Map<Long, Integer> productsQuantity = orderService.getProductsQuantity();

        List<ProductDto> allProducts = productService.getAllProducts().stream()
                .map(productConverter::convertModelToDto)
                .collect(Collectors.toList());

        // Sort the products based on the number of times they were sold
        allProducts.sort(Comparator.comparingInt(product -> productsQuantity.getOrDefault(product.getId(), 0)));

        // Reverse the list to get descending order
        Collections.reverse(allProducts);

        return allProducts;
    }

    @PostMapping("/fav")
    @Log
    public ResponseEntity<?> addToFavorites(@RequestParam("productId") Long productId) {
        productService.addToFavorites(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Product added to favorites successfully").build());
    }

    @DeleteMapping("/fav")
    @Log
    public ResponseEntity<?> removeFromFavorites(@RequestParam("productId") Long productId) {
        productService.removeFromFavorites(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Product removed from favorites successfully").build());
    }

    @GetMapping("/fav")
    @Log
    public ResponseEntity<List<ProductDto>> getFavProductsByLoggedInUser() {
        return ResponseEntity.ok(productService.getFavProductsByLoggedInUser());
    }

    @GetMapping("/search")
    @Log
    public ResponseEntity<PaginatedProductResponse> searchProductByName(@RequestParam("name") String name,
                                                                        @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(productService.searchProductByName(name, pageNumber, pageSize, sortBy));
    }
}
