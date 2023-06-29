package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.AppUserDto;
import com.qual.store.model.AppUser;
import com.qual.store.model.base.BaseEntity;
import com.qual.store.model.enums.RoleName;
import org.springframework.stereotype.Component;

@Component
public class AppUserConverter extends BaseConverter<AppUser, AppUserDto> {

    @Override
    public AppUser convertDtoToModel(AppUserDto dto) {
        return AppUser.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(RoleName.valueOf(dto.getRole()))
                .build();
    }

    @Override
    public AppUserDto convertModelToDto(AppUser appUser) {
        AppUserDto appUserDto = AppUserDto.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .email(appUser.getEmail())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .role(appUser.getRole().name())
                .orders(appUser.getOrders().stream().map(BaseEntity::getId).toList())
                .build();
        appUserDto.setId(appUser.getId());
        return appUserDto;
    }
}
