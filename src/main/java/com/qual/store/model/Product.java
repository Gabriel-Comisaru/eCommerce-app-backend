package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "productWithCategoryAndReviewsAndImages",
                attributeNodes = {
                        @NamedAttributeNode(value = "category"),
                        @NamedAttributeNode(value = "reviews"),
                        @NamedAttributeNode(value = "images"),
                        @NamedAttributeNode(value = "favoriteByUsers")
                }

        ),
        @NamedEntityGraph(
                name = "productWithUser",
                attributeNodes = {
                        @NamedAttributeNode(value = "user")
                }
        )
})
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product extends BaseEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column
    private long unitsInStock;

    @Column
    private double discountPercentage;

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @JsonIgnore
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.MERGE)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ImageModel> images;

    @ManyToMany(mappedBy = "favoriteProducts", cascade = CascadeType.ALL)
    private Set<AppUser> favoriteByUsers = new HashSet<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void addImageModel(ImageModel imageModel) {
        images.add(imageModel);
    }

    @Override
    public String toString() {
        return "Product{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", units in stock=" + unitsInStock +
                ", discount percentage=" + discountPercentage +
                '}' + super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}