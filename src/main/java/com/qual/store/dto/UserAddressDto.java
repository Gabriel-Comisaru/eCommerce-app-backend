package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAddressDto extends BaseDto implements Serializable {

    private String first_name;

    private String last_name;

    private String phone_number;


    private String address;
    private String city;

    private String county;

    private Long user_id;

    public UserAddressDto(String first_name,String last_name,String phone_number,String address,String city,String county)

    {
        this.phone_number = phone_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.city = city;
        this.county = county;
        this.address = address;

    }

}
