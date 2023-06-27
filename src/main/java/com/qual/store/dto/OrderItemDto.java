package com.qual.store.dto;

import com.qual.store.model.Product;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderItemDto extends BaseDto implements Serializable {
    private Integer quantity;
    private Long productId;
    private Long orderId;
}
