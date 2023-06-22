package console;

import console.ui.ProductConsole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ShopApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("console");

        ProductConsole productConsole = context.getBean(ProductConsole.class);
        productConsole.runConsole();
    }
}
