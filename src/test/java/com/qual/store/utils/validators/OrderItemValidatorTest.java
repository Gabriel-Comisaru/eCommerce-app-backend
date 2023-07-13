package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.OrderItem;
import com.qual.store.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderItemValidatorTest {
    private OrderItemValidator orderItemValidator;

    @BeforeEach
    void setUp() {
        orderItemValidator = new OrderItemValidator();
    }

    @Test
    void validate_ValidOrderItem_NoExceptionThrown() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(3);
        orderItem.setProduct(new Product());

        // when & then
        Assertions.assertDoesNotThrow(() -> orderItemValidator.validate(orderItem));
    }

    @Test
    void validate_NullOrderItem_ThrowsValidatorException() {
        // given
        OrderItem orderItem = null;

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderItemValidator.validate(orderItem));
    }

    @Test
    void validate_OrderItemWithNullQuantity_ThrowsValidatorException() {
        // given
        OrderItem orderItem = new OrderItem();

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderItemValidator.validate(orderItem));
    }

    @Test
    void validate_OrderItemWithNegativeQuantity_ThrowsValidatorException() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(-1);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderItemValidator.validate(orderItem));
    }

    @Test
    void validate_OrderItemWithZeroQuantity_ThrowsValidatorException() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(0);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderItemValidator.validate(orderItem));
    }

    @Test
    void validate_OrderItemWithNullProduct_ThrowsValidatorException() {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(3);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderItemValidator.validate(orderItem));
    }
}
