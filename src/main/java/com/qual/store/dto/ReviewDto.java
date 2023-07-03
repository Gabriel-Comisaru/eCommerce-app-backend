package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ReviewDto extends BaseDto {

    private double rating;

    private String title;

    private String comment;

    private LocalDateTime date;

    private Long productId;

    private Long userId;
}
