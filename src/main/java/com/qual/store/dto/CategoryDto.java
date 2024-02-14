package com.qual.store.dto;

import com.qual.store.dto.base.BaseDto;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryDto extends BaseDto implements Serializable {
    private String name;
    private List<Long> productIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto that = (CategoryDto) o;
        return Objects.equals(name, that.name) && Objects.equals(productIds, that.productIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, productIds);
    }
}
