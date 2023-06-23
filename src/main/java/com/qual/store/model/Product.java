package com.qual.store.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
public class Product extends BaseEntity<Long> {
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "category_id")
//    @ToString.Exclude
//    @JsonIgnore
//    private Category category;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;


}