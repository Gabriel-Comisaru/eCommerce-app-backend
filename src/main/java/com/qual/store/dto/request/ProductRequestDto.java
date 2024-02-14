package com.qual.store.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Invalid name: Empty name")
    @NotNull(message = "Invalid name: name is null")
    private String name;

    @NotBlank(message = "Invalid description: Empty description")
    @NotNull(message = "Invalid description: description is null")
    private String description;

    @NotNull(message = "Invalid price: price is null")
    @Min(value = 1, message = "Invalid price: Equals to zero or Less than zero")
    private Double price;

    @NotNull(message = "Invalid stock: stock is null")
    @Min(value = 0, message = "Invalid stock: Less than zero")
    private Long unitsInStock;

    @NotNull(message = "Invalid discount percentage: discount percentage is null")
    @Min(value = 0, message = "Invalid discount percentage: Less than 0")
    @Max(value = 100, message = "Invalid discount percentage: More than 100")
    private Double discountPercentage;

    private MultipartFile image;
}
