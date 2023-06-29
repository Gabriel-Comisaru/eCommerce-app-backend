package com.qual.store.converter.base;

import com.qual.store.converter.Converter;
import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.base.BaseEntity;

public abstract class BaseConverter<Model extends BaseEntity<Long>, Dto extends BaseDto>
        implements Converter<Model, Dto> {
}

