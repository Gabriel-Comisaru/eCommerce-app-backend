package com.qual.store.utils.validators;

import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.exceptions.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewValidatorTest {
    private ReviewValidator reviewValidator;

    @BeforeEach
    public void setup() {
        reviewValidator = new ReviewValidator();
    }

    @Test
    public void testValidate_NullReview() {
        // given
        ReviewRequestDto reviewRequestDto = null;

        // when & then
        assertThrows(ValidatorException.class, () -> {
            reviewValidator.validate(reviewRequestDto);
        });
    }

    @Test
    public void testValidate_NullTitle() {
        // given
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title(null)
                .build();

        // when & then
        assertThrows(ValidatorException.class, () -> {
            reviewValidator.validate(reviewRequestDto);
        });
    }

    @Test
    public void testValidate_EmptyTitle() {
        // given
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title("  ")
                .comment("Comment")
                .rating(1.0)
                .build();

        // when & then
        assertThrows(ValidatorException.class, () -> {
            reviewValidator.validate(reviewRequestDto);
        });
    }

    @Test
    public void testValidate_EmptyComment() {
        // given
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title("Title")
                .comment("")
                .build();

        // when & then
        assertThrows(ValidatorException.class, () -> {
            reviewValidator.validate(reviewRequestDto);
        });
    }

    @Test
    public void testValidate_NegativeRating() {
        // given
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .title("Title")
                .comment("Comment")
                .rating(-1.0)
                .build();

        // when & then
        assertThrows(ValidatorException.class, () -> {
            reviewValidator.validate(reviewRequestDto);
        });
    }
}