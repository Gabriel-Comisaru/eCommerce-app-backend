package com.qual.store.dto;

import com.qual.store.model.Category;
import com.qual.store.model.OrderItem;
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

    private String category_name;

    private List<Long> orderItems;
}
