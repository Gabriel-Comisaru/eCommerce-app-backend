package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product entity) throws ValidatorException {
        Map<Predicate<Product>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "product cannot cannot be null");
        conditions.put(cl -> cl != null && cl.getName() == null,
                "product name cannot be null");
        conditions.put(cl -> cl != null && cl.getName() != null && cl.getName().trim().isEmpty(),
                "product name cannot be empty");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }
}
