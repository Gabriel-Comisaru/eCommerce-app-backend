package com.qual.store.dto.paginated;

import com.qual.store.dto.ProductDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedProductResponse {

    private List<ProductDto> products;

    private Long numberOfItems;

    private int numberOfPages;
}
