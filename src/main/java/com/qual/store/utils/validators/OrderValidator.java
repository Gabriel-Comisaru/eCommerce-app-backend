package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Order;
import com.qual.store.model.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public class OrderValidator implements Validator<Order> {

    @Override
    public void validate(Order entity) throws ValidatorException {
        Map<Predicate<Order>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "order cannot cannot be null");
        // condition that the state of the order is part of the enum
        conditions.put(cl -> cl != null && cl.getStatus() == null,
                "order status cannot be null");
        conditions.put(cl -> cl != null && cl.getStatus() != null
                        && cl.getDeliveryPrice() < 0,
                "order delivery price cannot be negative");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }

    private boolean isOrderStatus(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
