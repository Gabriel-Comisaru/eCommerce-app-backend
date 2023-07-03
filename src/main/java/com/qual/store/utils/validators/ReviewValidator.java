package com.qual.store.utils.validators;

import com.qual.store.dto.request.ReviewRequestDto;
import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.Product;
import com.qual.store.model.Review;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public class ReviewValidator implements Validator<ReviewRequestDto> {
    @Override
    public void validate(ReviewRequestDto entity) throws ValidatorException {
        Map<Predicate<ReviewRequestDto>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "review cannot cannot be null");
        conditions.put(rev -> rev != null && rev.getTitle() == null,
                "review title cannot be null");
        conditions.put(cl -> cl != null && cl.getTitle() != null && cl.getComment().trim().isEmpty(),
                "review comment cannot be empty");
        conditions.put(cl -> cl != null && cl.getTitle() != null && cl.getComment().trim().isEmpty() && cl.getRating() < 0,
                "review rating cannot be negative");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }
}
