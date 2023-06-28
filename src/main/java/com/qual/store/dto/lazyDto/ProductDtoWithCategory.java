package com.qual.store.dto.lazyDto;

import com.qual.store.dto.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDtoWithCategory extends BaseDto implements Serializable {

    private String name;

    private String description;

    private double price;

    private Long categoryId;
}
