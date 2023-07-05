package com.qual.store.converter;

import com.qual.store.converter.base.BaseConverter;
import com.qual.store.dto.ImageModelDto;
import com.qual.store.model.ImageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.qual.store.utils.images.ImageUtils.compressBytes;
import static com.qual.store.utils.images.ImageUtils.decompressBytes;

@Component
@RequiredArgsConstructor
public class ImageConverter extends BaseConverter<ImageModel, ImageModelDto> {

    @Override
    public ImageModel convertDtoToModel(ImageModelDto dto) {
        return ImageModel.builder()
                .name(dto.getName())
                .type(dto.getType())
                .picByte(dto.getPicByte())
                .build();
    }

    @Override
    public ImageModelDto convertModelToDto(ImageModel imageModel) {
        ImageModelDto result = ImageModelDto.builder()
                .name(imageModel.getName())
                .type(imageModel.getType())
                .picByte(imageModel.getPicByte())
                .build();
        result.setId(imageModel.getId());

        return result;
    }
}
