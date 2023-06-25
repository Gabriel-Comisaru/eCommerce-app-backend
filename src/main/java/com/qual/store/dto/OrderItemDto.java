package com.qual.store.dto;

import com.qual.store.model.Product;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDto extends BaseDto implements Serializable {
    private Integer quantity;
    private Long productId;
}
