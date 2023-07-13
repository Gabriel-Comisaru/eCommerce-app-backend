package com.qual.store.dto.request;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequestDto implements Serializable {

    private double rating;

    private String title;

    private String comment;
}
