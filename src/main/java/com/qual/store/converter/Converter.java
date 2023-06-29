package com.qual.store.converter;


import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.base.BaseEntity;

public interface Converter<Model extends BaseEntity<Long>, Dto extends BaseDto> {
    Model convertDtoToModel(Dto dto);

    Dto convertModelToDto(Model model);
}
