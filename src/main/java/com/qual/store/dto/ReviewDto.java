package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ReviewDto extends BaseDto implements Serializable {

    private double rating;

    private String title;

    private String comment;

    private LocalDateTime date;

    private Long productId;

    private Long userId;

    private String username;

    private String fullName;
}
