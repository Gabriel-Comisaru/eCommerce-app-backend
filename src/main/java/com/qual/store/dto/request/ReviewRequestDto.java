package com.qual.store.dto.request;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ReviewRequestDto implements Serializable {

    private double rating;

    private String title;

    private String comment;
}
