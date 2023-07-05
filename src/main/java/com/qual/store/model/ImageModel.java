package com.qual.store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageModel extends BaseEntity<Long> {

    @Column(unique = true)
    private String name;
    
    private String type;

    @Column(name = "pic_byte", length = 1000)
    private byte[] picByte;

    @ManyToOne
    @JoinColumn(name = "product_id")
//    @JsonBackReference
    @JsonIgnore
    private Product product;
}
