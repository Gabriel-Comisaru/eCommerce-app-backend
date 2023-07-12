package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryValidatorTest {
    private CategoryValidator categoryValidator;

    @BeforeEach
    void setUp() {
        categoryValidator = new CategoryValidator();
    }

    @Test
    void validate_ValidCategory_NoExceptionThrown() {
        // given
        Category category = new Category();
        category.setName("Books");

        // when & then
        Assertions.assertDoesNotThrow(() -> categoryValidator.validate(category));
    }

    @Test
    void validate_NullCategory_ThrowsValidatorException() {
        // given
        Category category = null;

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> categoryValidator.validate(category));
    }

    @Test
    void validate_CategoryWithNullName_ThrowsValidatorException() {
        // given
        Category category = new Category();

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> categoryValidator.validate(category));
    }
}