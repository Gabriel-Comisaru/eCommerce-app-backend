package com.qual.store.dto;

import com.qual.store.model.Category;
import lombok.*;

import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto extends BaseDto implements Serializable {

    private String name;

    private String description;

    private double price;

    private Category category;
}
