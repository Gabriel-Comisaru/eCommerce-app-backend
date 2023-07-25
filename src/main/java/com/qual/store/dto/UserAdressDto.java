package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.AppUser;
import com.qual.store.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAdressDto extends BaseDto implements Serializable {

    private String first_name;

    private String last_name;

    private String phone_number;

    private String adresa;
    private String oras;

    private String judet;

    private Long user_id;

    public UserAdressDto(String first_name,String last_name,String phone_number,String adresa,String oras,String judet)
    {
        this.phone_number = phone_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.oras = oras;
        this.judet = judet;
        this.adresa = adresa;
    }

}
