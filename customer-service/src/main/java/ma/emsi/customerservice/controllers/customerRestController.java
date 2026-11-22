package ma.emsi.customerservice.controllers;


import ma.emsi.customerservice.entities.Customer;
import ma.emsi.customerservice.service.CustomerService;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController

public class customerRestController {

    private CustomerService customerService;
    public customerRestController(CustomerService customerService){
        this.customerService=customerService;
    }
    @GetMapping("/customers")
    public List<Customer> getAllCustomers(){

        return customerService.getAllCustomers();
    }
    @GetMapping("/customers/{id}")
    public EntityModel<Customer> findCustomerById(@PathVariable Long id){
        Customer customer = customerService.findCustomerById(id);

        // Les deux relations pointent vers la ressource du client, comme dans la vidéo.
        return EntityModel.of(customer,
                linkTo(methodOn(customerRestController.class).findCustomerById(id)).withSelfRel(),
                linkTo(methodOn(customerRestController.class).findCustomerById(id)).withRel("customer"));
    }
    @PostMapping("/customers")
    public Customer saveCostumer(@RequestBody Customer customer ){
        return customerService.saveCostumer(customer);
    }


}
