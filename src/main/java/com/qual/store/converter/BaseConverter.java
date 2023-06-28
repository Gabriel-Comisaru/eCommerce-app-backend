package com.qual.store.converter;

import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.BaseEntity;

public abstract class BaseConverter<Model extends BaseEntity<Long>, Dto extends BaseDto>
        implements Converter<Model, Dto> {
}

