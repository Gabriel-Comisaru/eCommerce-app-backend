package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Category;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public class CategoryValidator implements Validator<Category> {

    @Override
    public void validate(Category entity) throws ValidatorException {
        Map<Predicate<Category>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "category cannot cannot be null");
        conditions.put(cl -> cl != null && cl.getName() == null,
                "category name cannot be null");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }
}
