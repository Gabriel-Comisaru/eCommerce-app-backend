package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
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
    private Long categoryId;
    private Long productId;
    private Long orderId;
}
