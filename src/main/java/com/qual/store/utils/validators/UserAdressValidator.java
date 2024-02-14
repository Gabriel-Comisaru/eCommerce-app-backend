package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;
import com.qual.store.model.UserAddress;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
@Component
public class UserAdressValidator implements Validator<UserAddress>{
    @Override
    public void validate(UserAddress entity) throws ValidatorException {
        Map<Predicate<UserAddress>, String> conditions = new HashMap<>();
        conditions.put(Objects::isNull, "User address cannot be null");
        conditions.put(adress -> adress.getPhone_number() == null, "Phone number cannot be null");
        conditions.put(adress -> adress.getFirst_name() == null, "First name cannot be null");
        conditions.put(adress -> adress.getLast_name() == null, "Last name cannot be null");

        conditions.put(adress -> adress.getCity() == null, "Oras cannot be null");
        conditions.put(adress -> adress.getCounty() == null, "Judet cannot be null");

        conditions.put(adress -> adress.getPhone_number() != null && adress.getPhone_number().length() != 11,
                "Phone number must have exactly 11 characters");

        conditions.keySet().stream()
                .filter(s -> s.test(entity))
                .findFirst()
                .ifPresent(key -> {
                    throw new ValidatorException(conditions.get(key));
                });
    }
}
