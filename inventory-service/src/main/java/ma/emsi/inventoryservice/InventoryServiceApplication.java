package ma.emsi.inventoryservice;

import ma.emsi.inventoryservice.entities.Product;
import ma.emsi.inventoryservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(Product.builder().id(UUID.randomUUID().toString()).name("Computer").price(3200).quantity(11).build());
                repository.save(Product.builder().id(UUID.randomUUID().toString()).name("Printer").price(1299).quantity(10).build());
                repository.save(Product.builder().id(UUID.randomUUID().toString()).name("Smart Phone").price(5400).quantity(8).build());
            }
        };
    }
}
