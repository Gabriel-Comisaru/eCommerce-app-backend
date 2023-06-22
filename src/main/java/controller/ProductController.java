package controller;

import dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ProductService;
import utils.converter.ProductConverter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductConverter productConverter;

    @GetMapping()
    public List<ProductDto> getAllCProducts() {
        return productService.getAllProducts().stream()
                .map(product -> productConverter.convertModelToDto(product))
                .collect(Collectors.toList());
    }
}
