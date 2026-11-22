package ma.emsi.customerservice;

import ma.emsi.customerservice.entities.Customer;
import ma.emsi.customerservice.service.CustomerService;
import ma.emsi.customerservice.config.CustomerConfigParams;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
@EnableConfigurationProperties(CustomerConfigParams.class)


public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(CustomerService customerService){
        return args -> {
            List<String> names = List.of("aya","yassine","leila");
            names.forEach(name->{
               customerService.saveCostumer(Customer.builder()
                       .name(name)
                       .email(name+"@gmail.com")
                       .build());
            });
        };
    }


}
