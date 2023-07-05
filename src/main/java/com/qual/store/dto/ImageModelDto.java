package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class ImageModelDto extends BaseDto {
    private String name;

    private String type;

    private byte[] picByte;
}
