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
                )
        }
)
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Order extends BaseEntity<Long> {

    @Column(nullable = false)
    private double deliveryPrice;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    // TODO: User class + connection to Order DB
    @Column(nullable = false)
    private int userId;

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    @Override
    public String toString() {
        return "Order{" +
                "deliveryPrice=" + deliveryPrice +
                ", startDate=" + startDate +
                ", deliveryDate=" + deliveryDate +
                ", status=" + status +
                ", orderItems=" + orderItems +
                '}' + super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
