package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = "orderItemWithProduct",
                        attributeNodes = {
                                @NamedAttributeNode(value = "product")
                        }
                )
        }
)
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
public class OrderItem extends BaseEntity<Long> {

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;
}
