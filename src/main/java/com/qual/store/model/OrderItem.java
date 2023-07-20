package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qual.store.model.base.BaseEntity;
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
//    @JsonBackReference
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;


    //hashcode
    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
