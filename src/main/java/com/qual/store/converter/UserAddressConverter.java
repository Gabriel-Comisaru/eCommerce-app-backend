package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.UserAddressDto;
import com.qual.store.model.UserAddress;
import com.qual.store.repository.AppUserRepository;
import com.qual.store.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAddressConverter extends BaseConverter<UserAddress, UserAddressDto> {
    private final UserAddressRepository userAdressRepository;
    private final AppUserRepository appUserRepository;
    @Override
    public UserAddress convertDtoToModel(UserAddressDto dto) {
        return UserAddress.builder()
                .first_name(dto.getFirst_name())
                .last_name(dto.getLast_name())
                .phone_number(dto.getPhone_number())
                .user(appUserRepository.findById(dto.getUser_id()).orElse(null))
                .county(dto.getCounty())
                .address(dto.getAddress())
                .city(dto.getCity())
                .build();
    }

    @Override
    public UserAddressDto convertModelToDto(UserAddress userAddress) {
        UserAddressDto userAdressDto = UserAddressDto.builder()
                .first_name(userAddress.getFirst_name())
                .last_name(userAddress.getLast_name())
                .phone_number(userAddress.getPhone_number())

                .county(userAddress.getCounty())
                .city(userAddress.getCity())
                .address(userAddress.getAddress())
                .user_id(userAddress.getUser().getId())
                .build();
        userAdressDto.setId(userAddress.getId());
        return userAdressDto;
    }
}
