package ma.emsi.customerservice.repository;

import ma.emsi.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "customers", collectionResourceRel = "customers")
public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
