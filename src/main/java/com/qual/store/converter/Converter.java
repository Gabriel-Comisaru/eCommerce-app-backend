package com.qual.store.converter;


import com.qual.store.dto.BaseDto;
import com.qual.store.model.BaseEntity;

public interface Converter<Model extends BaseEntity<Long>, Dto extends BaseDto> {
    Model convertDtoToModel(Dto dto);

    Dto convertModelToDto(Model model);

}
