package com.qual.store.service.impl;

import com.qual.store.converter.ReviewConverter;
import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.exceptions.ReviewNotFoundException;
import com.qual.store.model.AppUser;
import com.qual.store.model.Product;
import com.qual.store.model.Review;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.ProductRepository;
import com.qual.store.repository.ReviewRepository;
import com.qual.store.utils.validators.ReviewValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewConverter reviewConverter;

    @Mock
    private ReviewValidator reviewValidator;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllReviewsTest() {
        // given
        List<Review> reviewList = new ArrayList<>();
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        reviewList.add(review1);
        reviewList.add(review2);

        ReviewDto reviewDto1 = ReviewDto.builder().build();
        ReviewDto reviewDto2 = ReviewDto.builder().build();

        // when
        when(reviewRepository.findAll()).thenReturn(reviewList);
        when(reviewConverter.convertModelToDto(review1)).thenReturn(reviewDto1);
        when(reviewConverter.convertModelToDto(review2)).thenReturn(reviewDto2);
        List<ReviewDto> result = reviewService.getAllReviews();

        result.forEach(System.out::println);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(reviewDto1));
        assertTrue(result.contains(reviewDto2));
        verify(reviewRepository, times(1)).findAll();
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void testGetReviewsByProductId() {
        // given
        Long productId = 1L;
        List<Review> reviewList = new ArrayList<>();
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        reviewList.add(review1);
        reviewList.add(review2);

        ReviewDto reviewDto1 = ReviewDto.builder().build();
        ReviewDto reviewDto2 = ReviewDto.builder().build();

        Product product = new Product();
        product.setId(productId);
        review1.setProduct(product);
        review2.setProduct(product);

        // when
        when(reviewRepository.findAll()).thenReturn(reviewList);
        when(reviewConverter.convertModelToDto(review1)).thenReturn(reviewDto1);
        when(reviewConverter.convertModelToDto(review2)).thenReturn(reviewDto2);
        List<ReviewDto> result = reviewService.getReviewsByProductId(productId);

        // then
        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAll();
        verifyNoMoreInteractions(reviewRepository);
    }


    @Test
    public void testSaveReview() {
        // given
        Long productId = 1L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder().build();
        Product product = Product.builder()
                .reviews(new ArrayList<>()).build();
        product.setId(productId);
        Review review = Review.builder()
                .title("Title")
                .product(product)
                .build();
        ReviewDto savedReviewDto = ReviewDto.builder().build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(new AppUser(), new Object());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewConverter.convertRequestDtoToModel(reviewRequestDto)).thenReturn(review);
        when(appUserRepository.findUserByUsername(anyString())).thenReturn(new AppUser());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(reviewRepository.findByTitle(anyString())).thenReturn(Optional.of(review));
        when(reviewConverter.convertModelToDto(review)).thenReturn(savedReviewDto);

        // when
        ReviewDto result = reviewService.saveReview(productId, reviewRequestDto);

        // then
        assertNotNull(result);
        verify(reviewValidator, times(1)).validate(reviewRequestDto);
        verify(reviewRepository, times(1)).findByTitle(review.getTitle());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void updateReviewTest() {
        // given
        Long reviewId = 1L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder().build();
        Review review = new Review();
        Optional<Review> reviewOptional = Optional.of(review);
        ReviewDto reviewDto = ReviewDto.builder().build();

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(reviewOptional);
        when(reviewConverter.convertModelToDto(review)).thenReturn(reviewDto);
        ReviewDto result = reviewService.updateReview(reviewId, reviewRequestDto);

        // then
        assertNotNull(result);
        verify(reviewValidator, times(1)).validate(reviewRequestDto);
        verify(reviewRepository, times(2)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void updateReviewThrowsReviewNotFoundExceptionTest() {
        // given
        Long reviewId = 1L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder().build();
        Optional<Review> reviewOptional = Optional.empty();

        // when & then
        when(reviewRepository.findById(reviewId)).thenReturn(reviewOptional);
        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, reviewRequestDto);
        });
        verify(reviewValidator, times(1)).validate(reviewRequestDto);
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void findReviewByIdTest() {
        // given
        Long reviewId = 1L;
        Review review = new Review();
        ReviewDto reviewDto = ReviewDto.builder().build();

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewConverter.convertModelToDto(review)).thenReturn(reviewDto);
        ReviewDto result = reviewService.findReviewById(reviewId);

        // then
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void findReviewByIdThrowsReviewNotFoundTest() {
        // given
        Long reviewId = 1L;

        // when & then
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.findReviewById(reviewId);
        });
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void deleteReviewByIdTest() {
        // given
        Long reviewId = 1L;
        Review review = new Review();
        Optional<Review> reviewOptional = Optional.of(review);
        Product product = Product.builder()
                .reviews(new ArrayList<>())
                .build();
        product.setId(1L);
        product.addReview(review);
        review.setProduct(product);

        // when
        when(reviewRepository.findById(reviewId)).thenReturn(reviewOptional);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        reviewService.deleteReviewById(reviewId);

        // then
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(productRepository, times(1)).findById(anyLong());
        verify(reviewRepository, times(1)).delete(review);
        verify(productRepository, times(1)).save(product);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void deleteReviewByIdThrowsReviewNotFoundExceptionTest() {
        // given
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReviewById(reviewId);
        });
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }
}