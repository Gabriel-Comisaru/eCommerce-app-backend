package com.qual.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qual.store.dto.ReviewDto;
import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private AutoCloseable closeable;

    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(reviewController)
                .build();
    }

    @Test
    public void getAllReviewsTest() throws Exception {
        // given
        ReviewDto firstReviewDto = ReviewDto.builder().rating(4.5).title("Great product").comment("Awesome!").build();
        firstReviewDto.setId(1L);

        ReviewDto secondReviewDto = ReviewDto.builder().rating(3.5).title("Average product").comment("It's okay").build();
        secondReviewDto.setId(2L);

        List<ReviewDto> reviews = Arrays.asList(
                firstReviewDto,
                secondReviewDto
        );

        // when
        when(reviewService.getAllReviews()).thenReturn(reviews);
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].rating", is(4.5)))
                .andExpect(jsonPath("$[0].title", is("Great product")))
                .andExpect(jsonPath("$[0].comment", is("Awesome!")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].rating", is(3.5)))
                .andExpect(jsonPath("$[1].title", is("Average product")))
                .andExpect(jsonPath("$[1].comment", is("It's okay")));

        // then
        verify(reviewService, times(1)).getAllReviews();
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void getReviewsByProductIdTest() throws Exception {
        // given
        Long productId = 1L;

        ReviewDto firstReviewDto = ReviewDto.builder().rating(4.5).title("Great product").comment("Awesome!").build();
        firstReviewDto.setId(1L);

        ReviewDto secondReviewDto = ReviewDto.builder().rating(3.5).title("Average product").comment("It's okay").build();
        secondReviewDto.setId(2L);

        List<ReviewDto> reviews = Arrays.asList(
                firstReviewDto,
                secondReviewDto
        );

        // when
        when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);
        mockMvc.perform(get("/api/reviews/product/{productId}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].rating", is(4.5)))
                .andExpect(jsonPath("$[0].title", is("Great product")))
                .andExpect(jsonPath("$[0].comment", is("Awesome!")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].rating", is(3.5)))
                .andExpect(jsonPath("$[1].title", is("Average product")))
                .andExpect(jsonPath("$[1].comment", is("It's okay")));

        // then
        verify(reviewService, times(1)).getReviewsByProductId(productId);
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void testGetReviewById() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewDto review = ReviewDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .build();
        review.setId(reviewId);


        // when
        when(reviewService.findReviewById(reviewId)).thenReturn(review);
        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.title", is("Great product")))
                .andExpect(jsonPath("$.comment", is("Awesome!")));

        // then
        verify(reviewService, times(1)).findReviewById(reviewId);
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void saveReviewTest() throws Exception {
        // given
        Long productId = 1L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .build();
        ReviewDto savedReview = ReviewDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .date(LocalDateTime.now())
                .productId(productId)
                .userId(1L)
                .build();

        savedReview.setId(1L);

        // when
        when(reviewService.saveReview(productId, reviewRequestDto)).thenReturn(savedReview);
        mockMvc.perform(post("/api/reviews/save/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.title", is("Great product")))
                .andExpect(jsonPath("$.comment", is("Awesome!")));

        // then
        verify(reviewService, times(1)).saveReview(productId, reviewRequestDto);
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void updateReviewTest() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .build();
        ReviewDto updatedReview = ReviewDto.builder()
                .rating(4.5)
                .title("Great product")
                .comment("Awesome!")
                .build();
        updatedReview.setId(reviewId);

        // when
        when(reviewService.updateReview(reviewId, reviewRequestDto)).thenReturn(updatedReview);
        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.title", is("Great product")))
                .andExpect(jsonPath("$.comment", is("Awesome!")));

        // then
        verify(reviewService, times(1)).updateReview(reviewId, reviewRequestDto);
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void deleteReviewTest() throws Exception {
        // Arrange
        Long reviewId = 1L;

        // Act
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("review with id = 1 deleted")));

        // Assert
        verify(reviewService, times(1)).deleteReviewById(reviewId);
        verifyNoMoreInteractions(reviewService);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}