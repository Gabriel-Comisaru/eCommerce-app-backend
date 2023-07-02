package com.qual.store.model;

import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = "userWithOrders",
                        attributeNodes = {
                                @NamedAttributeNode(value = "orders")
                        }
                ),
                @NamedEntityGraph(
                        name = "userWithProducts",
                        attributeNodes = {
                                @NamedAttributeNode(value = "products")
                        }
                )
        }
)
@Table(name = "app_users")
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@Builder
public class AppUser extends BaseEntity<Long> {

    @Column(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleName role;

    public AppUser() {
        this.role = RoleName.USER;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUser appUser = (AppUser) o;
        return getId() != null && Objects.equals(getId(), appUser.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
