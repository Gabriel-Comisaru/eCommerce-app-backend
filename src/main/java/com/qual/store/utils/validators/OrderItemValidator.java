package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public class OrderItemValidator implements Validator<OrderItem> {

    @Override
    public void validate(OrderItem entity) throws ValidatorException {
        Map<Predicate<OrderItem>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "orderItem cannot cannot be null");
        conditions.put(cl -> cl != null && cl.getQuantity() == null,
                "orderItem quantity cannot be null");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }
}
