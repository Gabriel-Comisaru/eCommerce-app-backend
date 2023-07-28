package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private Long userId;
    private String userName;
    private String userEmail;
    private List<Long> orderItems;
    //private List<String> productNames;
    private Map<Long, String> productIdsAndNames;
}
