package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class Review extends BaseEntity<Long> {

    private double rating;

    @Column(unique = true)
    private String title;

    private String comment;

    @CreationTimestamp
    private LocalDateTime date;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private AppUser user;
}
