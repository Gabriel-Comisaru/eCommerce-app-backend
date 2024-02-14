package com.qual.store.converter;

import com.qual.store.dto.ProductDto;
import com.qual.store.dto.request.ProductRequestDto;
import com.qual.store.model.*;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.CategoryRepository;
import com.qual.store.repository.ImageRepository;
import com.qual.store.repository.ReviewRepository;
import com.qual.store.utils.ProductRatingCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductConverterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRatingCalculator productRatingCalculator;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ProductConverter productConverter;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertDtoToModelTest() {
        // given
        Long categoryId = 1L;
        Long userId = 2L;
        List<Long> reviewIds = List.of(3L, 4L);
        List<String> imageNames = List.of("image1.jpg", "image2.jpg");

        ProductDto productDto = ProductDto.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10)
                .discountPercentage(0.1)
                .categoryId(categoryId)
                .userId(userId)
                .reviewsId(reviewIds)
                .imagesName(imageNames)
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();
        category.setId(categoryId);

        Product expectedProduct = Product.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10)
                .discountPercentage(0.1)
                .category(category)
                .user(null)
                .reviews(new ArrayList<>())
                .images(new HashSet<>())
                .build();

        // when
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());
        when(reviewRepository.findById(3L)).thenReturn(Optional.of(new Review()));
        when(reviewRepository.findById(4L)).thenReturn(Optional.of(new Review()));
        when(imageRepository.findByName("image1.jpg")).thenReturn(Optional.of(new ImageModel()));
        when(imageRepository.findByName("image2.jpg")).thenReturn(Optional.of(new ImageModel()));
        Product actualProduct = productConverter.convertDtoToModel(productDto);

        // then
        assertEquals(expectedProduct, actualProduct);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(appUserRepository, times(1)).findById(userId);
        verify(reviewRepository, times(1)).findById(3L);
        verify(reviewRepository, times(1)).findById(4L);
        verify(imageRepository, times(1)).findByName("image1.jpg");
        verify(imageRepository, times(1)).findByName("image2.jpg");
    }

    @Test
    public void convertModelToDtoTest() {
        // given
        Long productId = 1L;
        Long categoryId = 2L;
        Long userId = 3L;
        List<Long> reviewIds = List.of(4L, 5L);
        List<String> imageNames = List.of("image3.jpg", "image4.jpg");
        AppUser user = new AppUser();
        user.setId(userId);

        Category category = Category.builder().name("Category").build();
        category.setId(categoryId);

        Review reviewOne = new Review();
        reviewOne.setId(4L);
        Review reviewTwo = new Review();
        reviewTwo.setId(5L);

        ImageModel imageModelOne = ImageModel.builder().name("image3.jpg").build();
        ImageModel imageModelTwo = ImageModel.builder().name("image4.jpg").build();

        Product product = Product.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10)
                .discountPercentage(0.1)
                .category(category)
                .user(user)
                .reviews(new ArrayList<>(List.of(reviewOne, reviewTwo)))
                .images(new HashSet<>(List.of(imageModelOne, imageModelTwo)))
                .favoriteByUsers(new HashSet<>())
                .build();

        product.setId(productId);

        ProductDto expectedProductDto = ProductDto.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10)
                .discountPercentage(0.1)
                .rating(5.0)
                .userId(userId)
                .createTime(null)
                .updateTime(null)
                .orderItems(new ArrayList<>())
                .categoryId(categoryId)
                .categoryName("Category")
                .reviewsId(reviewIds)
                .imagesName(imageNames)
                .favUserIds(new ArrayList<>())
                .build();

        expectedProductDto.setId(productId);

        // when
        when(productRatingCalculator.calculateRating(any())).thenReturn(5.0);
        ProductDto actualProductDto = productConverter.convertModelToDto(product);

        // then
        assertEquals(expectedProductDto.getName(), actualProductDto.getName());
        assertEquals(expectedProductDto.getDescription(), actualProductDto.getDescription());
        assertEquals(expectedProductDto.getPrice(), actualProductDto.getPrice());
        assertEquals(expectedProductDto.getUnitsInStock(), actualProductDto.getUnitsInStock());
        assertEquals(expectedProductDto.getDiscountPercentage(), actualProductDto.getDiscountPercentage());
        assertEquals(expectedProductDto.getRating(), actualProductDto.getRating());
        assertEquals(expectedProductDto.getCategoryId(), actualProductDto.getCategoryId());
        assertEquals(expectedProductDto.getCategoryName(), actualProductDto.getCategoryName());
        assertEquals(expectedProductDto.getUserId(), actualProductDto.getUserId());
        assertEquals(expectedProductDto.getReviewsId(), actualProductDto.getReviewsId());
        assertEquals(expectedProductDto.getImagesName(), actualProductDto.getImagesName());
    }

    @Test
    public void convertRequestToModelTest() {
        // given
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10L)
                .discountPercentage(0.1)
                .build();

        Product expectedProduct = Product.builder()
                .name("Product")
                .description("Product description")
                .price(9.99)
                .unitsInStock(10)
                .discountPercentage(0.1)
                .images(new HashSet<>())
                .reviews(new ArrayList<>())
                .orderItems(new HashSet<>())
                .build();

        // when
        Product actualProduct = productConverter.convertRequestToModel(productRequestDto);

        // then
        assertEquals(expectedProduct, actualProduct);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}
