package com.qual.store.dto.paginated;

import com.qual.store.dto.OrderDto;
import com.qual.store.dto.ProductDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedOrderResponse {

    private List<OrderDto> orders;

    private Long numberOfItems;

    private int numberOfPages;
}
