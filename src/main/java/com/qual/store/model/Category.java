package com.qual.store.model;

import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = "categoryWithProducts",
                        attributeNodes = {
                                @NamedAttributeNode(value = "products")
                        }
                )
        }
)
@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
public class Category extends BaseEntity<Long> {

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "category")
    private List<Product> products;
}
