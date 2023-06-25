package com.qual.store.dto;

import com.qual.store.model.Product;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryDto extends BaseDto implements Serializable {
    private String name;
    private List<Product> products;
}
