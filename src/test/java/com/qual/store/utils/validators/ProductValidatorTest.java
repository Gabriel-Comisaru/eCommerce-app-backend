package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductValidatorTest {
    private ProductValidator productValidator;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
    }

    @Test
    void validate_ValidProduct_NoExceptionThrown() {
        // given
        Product product = new Product();
        product.setName("Book");
        product.setPrice(9.99);

        // when & then
        Assertions.assertDoesNotThrow(() -> productValidator.validate(product));
    }

    @Test
    void validate_NullProduct_ThrowsValidatorException() {
        // given
        Product product = null;

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> productValidator.validate(product));
    }

    @Test
    void validate_ProductWithNullName_ThrowsValidatorException() {
        // given
        Product product = new Product();
        product.setPrice(9.99);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> productValidator.validate(product));
    }

    @Test
    void validate_ProductWithEmptyName_ThrowsValidatorException() {
        // given
        Product product = new Product();
        product.setName("");
        product.setPrice(9.99);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> productValidator.validate(product));
    }
}