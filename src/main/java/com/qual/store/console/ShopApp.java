package com.qual.store.console;

import com.qual.store.console.ui.ProductConsole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ShopApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com/qual/store/console");

        ProductConsole productConsole = context.getBean(ProductConsole.class);
        productConsole.runConsole();
    }
}
