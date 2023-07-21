package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto extends BaseDto implements Serializable {

    private String name;
    private String description;
    private double price;
    private long unitsInStock;
    private double discountPercentage;
    private double rating;
    private Date createTime;
    private Date updateTime;
    private List<Long> orderItems;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private List<Long> reviewsId;
    private List<String> imagesName;
    private List<Long> favUserIds;
}
