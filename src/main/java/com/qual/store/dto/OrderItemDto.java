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
    private String categoryName;
    private Long productId;
    private String productName;
    private double productPrice;
    private long unitsInStock;
    private Long orderId;
    private String imageName;
}
