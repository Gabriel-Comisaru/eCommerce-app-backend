package com.qual.store.service.impl;

import com.qual.store.converter.ProductConverter;
import com.qual.store.dto.ProductDto;
import com.qual.store.dto.paginated.PaginatedProductResponse;
import com.qual.store.exceptions.CategoryNotFoundException;
import com.qual.store.exceptions.DeleteProductException;
import com.qual.store.exceptions.ImageModelException;
import com.qual.store.exceptions.ProductNotFoundException;
import com.qual.store.logger.Log;
import com.qual.store.model.*;
import com.qual.store.repository.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.qual.store.utils.images.ImageUtils.compressBytes;

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

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    @Log
    public Product saveProductCategory(String name, String description, double price,
                                       long unitsInStock, double discountPercentage,
                                       MultipartFile file, Long categoryId) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .unitsInStock(unitsInStock)
                .discountPercentage(discountPercentage)
                .images(new HashSet<>())
                .reviews(new ArrayList<>())
                .orderItems(new HashSet<>())
                .build();

        validator.validate(product);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("No category found with id %s", categoryId)));

        product.setCategory(category);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        AppUser appUser = appUserRepository.findUserByUsername(currentUsername);
        product.setUser(appUser);

        ImageModel imageModel;
        try {
            imageModel = getImageModelFromMultipartFile(file);
        } catch (IOException e) {
            throw new ImageModelException(e.getMessage());
        }

        product.addImageModel(imageModel);

        Product savedProduct = productRepository.save(product);
        imageModel.setProduct(savedProduct);

        imageRepository.save(imageModel);
        return savedProduct;
    }

    private ImageModel getImageModelFromMultipartFile(MultipartFile file) throws IOException {
        return ImageModel.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .picByte(compressBytes(file.getBytes()))
                .build();
    }

    @Override
    @Log
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategoryAndReviewsAndImages();
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

        Optional<Product> optionalProduct = productRepository.findAllWithCategoryAndReviewsAndImages()
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
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product found with id %s", id)));
    }

    @Override
    @Transactional
    @Log
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("No product found with id %s", id)));

        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow();

        category.getProducts().remove(product);
        categoryRepository.save(category);

        product.getOrderItems().stream()
                .findFirst()
                .ifPresent((element) -> {
                    throw new DeleteProductException("Cannot delete the product because it appears on order items.");
                });

        List<Review> reviews = product.getReviews();
        reviewRepository.deleteAll(reviews);

        Set<ImageModel> imageModels = product.getImages();
        imageRepository.deleteAll(imageModels);


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
        Product product = productRepository.findAllWithCategoryAndReviewsAndImages()
                .stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format("No product found with id %s", productId))
                );

        return productConverter.convertModelToDto(product);
    }
    //find products by category
    @Override
    @Log
    public List<Product> findProductsByCategory(Long categoryId) {
        return productRepository.findAllWithCategoryAndReviewsAndImages()
                .stream()
                .filter(product -> product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }


}
