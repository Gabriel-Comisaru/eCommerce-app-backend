package com.qual.store.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto extends BaseDto implements Serializable {

    private String name;

    private String description;

    private double price;

    private List<Long> orderItems;
    private Long categoryId;
}
