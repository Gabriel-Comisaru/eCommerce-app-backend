package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "order")
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    // TODO: User class + connection to Order DB
    @Column(nullable = false)
    private int userId;

    public Set<OrderItem> getOrderItem() {
        return orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public void addOrderItems(Set<OrderItem> orderItems) {
        orderItems.forEach(this::addOrderItem);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
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
