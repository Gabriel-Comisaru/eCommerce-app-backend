package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.UserAdressDto;
import com.qual.store.dto.base.BaseDto;
import com.qual.store.model.UserAdress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.UserAdressRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdressConverter extends BaseConverter<UserAdress, UserAdressDto> {
    private final UserAdressRepository userAdressRepository;
    private final AppUserRepository appUserRepository;
    @Override
    public UserAdress convertDtoToModel(UserAdressDto dto) {
        return UserAdress.builder()
                .first_name(dto.getFirst_name())
                .last_name(dto.getLast_name())
                .phone_number(dto.getPhone_number())
                .user(appUserRepository.findById(dto.getUser_id()).orElse(null))
                .judet(dto.getJudet())
                .adresa(dto.getAdresa())
                .oras(dto.getOras())
                .build();
    }

    @Override
    public UserAdressDto convertModelToDto(UserAdress userAdress) {
        UserAdressDto userAdressDto = UserAdressDto.builder()
                .first_name(userAdress.getFirst_name())
                .last_name(userAdress.getLast_name())
                .phone_number(userAdress.getPhone_number())
                .judet(userAdress.getJudet())
                .oras(userAdress.getOras())
                .adresa(userAdress.getAdresa())
                .user_id(userAdress.getUser().getId())
                .build();
        userAdressDto.setId(userAdress.getId());
        return userAdressDto;
    }
}
