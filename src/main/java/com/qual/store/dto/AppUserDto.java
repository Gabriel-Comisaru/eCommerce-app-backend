package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.enums.RoleName;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AppUserDto extends BaseDto implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private List<Long> orders;
}
