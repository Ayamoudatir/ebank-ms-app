package ma.emsi.ebankservice.service;

import ma.emsi.ebankservice.client.CustomerClient;
import ma.emsi.ebankservice.model.Customer;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerLookupService {

    private final CustomerClient customerClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public CustomerLookupService(CustomerClient customerClient,
                                 CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.customerClient = customerClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public Customer findCustomerById(Long id) {
        return circuitBreakerFactory.create("customerService")
                .run(() -> customerClient.findCustomerById(id), error -> unavailableCustomer(id));
    }

    private Customer unavailableCustomer(Long id) {
        return Customer.builder()
                .id(id)
                .name("Customer indisponible")
                .build();
    }
}
