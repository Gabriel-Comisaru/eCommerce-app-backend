package com.qual.store.model;

import com.github.javafaker.App;
import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "useradress")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAdress extends BaseEntity<Long> {
        @Column
        private String first_name;
        @Column
        private String last_name;
        @Column
        private String phone_number;
        @Column
        private String adresa;
        @Column
        private String oras;
        @Column
        private String judet;
        @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
        @JoinColumn(name = "user_id")
        private AppUser user;



}
