package com.qual.store.service;

import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getAllProductsByDiscount();

    List<Product> getAllProductsByPriceRange(Double minPrice, Double maxPrice);

    Product saveProductCategory(ProductRequestDto productRequestDto, Long categoryId);

    void saveProduct(Product product);

    Product updateProduct(Long id, ProductRequestDto productRequestDto);

    Product findProductById(Long id);

    void deleteProductById(Long id);

    PaginatedProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy);

    ProductDto getProductById(Long productId);

    List<Product> findProductsByCategory(Long categoryId);

    void addToFavorites(Long productId);

    void removeFromFavorites(Long productId);

    List<ProductDto> getFavProductsByLoggedInUser();

    PaginatedProductResponse searchProductByName(String name, Integer pageNumber, Integer pageSize, String sortBy);
}
