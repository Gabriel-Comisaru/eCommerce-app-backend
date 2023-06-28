package com.qual.store.dto.lazyDto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderItemWithProductDto extends BaseDto implements Serializable {

    private Integer quantity;

    private Long productId;
}
