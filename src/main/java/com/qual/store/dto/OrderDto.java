package com.qual.store.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDto extends BaseDto implements Serializable {
    private double deliveryPrice;
    private LocalDate startDate;
    private LocalDate deliveryDate;
    private String status;
    private int userId;
    private List<Long> orderItems;
}