package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Order;
import com.qual.store.model.enums.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderValidatorTest {
    private OrderValidator orderValidator;

    @BeforeEach
    void setUp() {
        orderValidator = new OrderValidator();
    }

    @Test
    void validate_ValidOrder_NoExceptionThrown() {
        // given
        Order order = new Order();
        order.setDeliveryPrice(9.99);
        order.setStatus(OrderStatus.ACTIVE);

        // when & then
        Assertions.assertDoesNotThrow(() -> orderValidator.validate(order));
    }

    @Test
    void validate_NullOrder_ThrowsValidatorException() {
        // given
        Order order = null;

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderValidator.validate(order));
    }

    @Test
    void validate_OrderWithNullStatus_ThrowsValidatorException() {
        // given
        Order order = new Order();
        order.setDeliveryPrice(9.99);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderValidator.validate(order));
    }

    @Test
    void validate_OrderWithNegativeDeliveryPrice_ThrowsValidatorException() {
        // given
        Order order = new Order();
        order.setDeliveryPrice(-9.99);
        order.setStatus(OrderStatus.ACTIVE);

        // when & then
        Assertions.assertThrows(ValidatorException.class, () -> orderValidator.validate(order));
    }

}
