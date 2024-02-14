package com.qual.store.converter;

import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.model.AppUser;
import com.qual.store.model.Product;
import com.qual.store.model.Review;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReviewConverterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private ReviewConverter reviewConverter;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConvertDtoToModel() {
        // given
        ReviewDto reviewDto = ReviewDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .date(LocalDateTime.now())
                .productId(1L)
                .userId(2L)
                .build();

        Product product = new Product();
        product.setId(1L);

        AppUser user = new AppUser();
        user.setId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(appUserRepository.findById(2L)).thenReturn(Optional.of(user));

        // when
        Review result = reviewConverter.convertDtoToModel(reviewDto);

        // then
        assertEquals(4.5, result.getRating());
        assertEquals("Great product", result.getTitle());
        assertEquals("Awesome!", result.getComment());
        assertEquals(reviewDto.getDate(), result.getDate());
        assertEquals(product, result.getProduct());
        assertEquals(2L, result.getUser().getId());
    }

    @Test
    public void testConvertModelToDto() {
        // given
        Review review = new Review();
        review.setId(1L);
        review.setRating(4.5);
        review.setTitle("Great product");
        review.setComment("Awesome!");
        review.setDate(LocalDateTime.now());

        Product product = new Product();
        product.setId(1L);
        review.setProduct(product);

        AppUser user = new AppUser();
        user.setId(2L);
        user.addReview(review);
        review.setUser(user);

        // when
        ReviewDto result = reviewConverter.convertModelToDto(review);

        // then
        assertEquals(4.5, result.getRating());
        assertEquals("Great product", result.getTitle());
        assertEquals("Awesome!", result.getComment());
        assertEquals(review.getDate(), result.getDate());
        assertEquals(1L, result.getProductId());
        assertEquals(2L, result.getUserId());
    }

    @Test
    public void testConvertRequestDtoToModel() {
        // given
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .build();

        // when
        Review result = reviewConverter.convertRequestDtoToModel(reviewRequestDto);

        // then
        assertEquals(4.5, result.getRating());
        assertEquals("Great product", result.getTitle());
        assertEquals("Awesome!", result.getComment());
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}