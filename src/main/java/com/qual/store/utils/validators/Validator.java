package com.qual.store.utils.validators;

import com.qual.store.exceptions.ValidatorException;

public interface Validator<T> {
    void validate(T entity) throws ValidatorException;
}