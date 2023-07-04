package com.qual.store.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewRequestDto {

    private double rating;

    private String title;

    private String comment;
}
