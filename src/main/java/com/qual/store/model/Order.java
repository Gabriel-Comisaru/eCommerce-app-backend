package com.qual.store.model;

import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = "orderWithOrderItems",
                        attributeNodes = {
                                @NamedAttributeNode(value = "orderItems")
                        }
                ),
                @NamedEntityGraph(
                        name = "orderWithUser",
                        attributeNodes = {
                                @NamedAttributeNode(value = "user")
                        }
                )
        }
)
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Builder
@ToString(callSuper = true)
public class Order extends BaseEntity<Long> {

    @Column(nullable = false)
    private double deliveryPrice;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    // TODO: User class + connection to Order DB
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private AppUser user;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
