package com.qual.store.console.ui;

import com.qual.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductConsole {
    @Autowired
    private RestTemplate restTemplate;
    public void runConsole() {
        System.out.println("ui console .............");

        Product ProductDto = restTemplate.getForObject("http://localhost:8080/api/products", Product.class);
        System.out.println(ProductDto);
    }
}
